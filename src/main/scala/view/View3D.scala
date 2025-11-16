package view

import scala.collection.immutable.Map as ScalaMap
import javax.swing.*
import java.awt.*
import java.awt.image.*
import core.*

class View3D(game: Game) extends JPanel{
  private val frame = new JFrame("RayBlaster3D")
  frame.addKeyListener(game.keyHandler)
  frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE)

  val screenX = game.screenX
  val screenY = 600
  val texSize = TextureManager.tileSize

  private val textureCache = scala.collection.mutable.Map[(Int, Int), Image]()
  private val buffer = new BufferedImage(screenX, screenY, BufferedImage.TYPE_INT_RGB)

  frame.setSize(screenX, screenY)
  frame.add(this)
  frame.setVisible(true)

  override def paintComponent(g: Graphics): Unit = { ///HUHHUHHUH. En ois uskonu et joudun kirjottaa sekä buffering että two pass rendering algoritmin :DD
    super.paintComponent(g)

    val bg = buffer.getGraphics.asInstanceOf[Graphics2D]

    val player = game.player
    // background
    bg.setColor(Color.CYAN)
    bg.fillRect(0, 0, screenX, screenY)

    // Lasketaan et mis kohtaa yhen seinän pystysuoran suikaleen alku ja loppu y on
    val wallBounds = Array.ofDim[(Int, Int)](screenX)
    for (i <- game.rays.indices) {
      val rayColumn = game.rays(i)
      rayColumn.firstOpaqueHit(game.map) match {
        case Some(firstHit) =>
          val rayHeight = ((screenY / firstHit.fixedDistance)).toInt
          val rayPosX = i * game.pixelsPerRay
          val rayTopY = (screenY / 2) - (rayHeight / 2)
          val rayBottomY = rayTopY + rayHeight
          for (px <- rayPosX until (rayPosX + game.pixelsPerRay).min(screenX)) {
            wallBounds(px) = (rayTopY, rayBottomY)
          }
        case None =>
          val rayPosX = i * game.pixelsPerRay
          for (px <- rayPosX until (rayPosX + game.pixelsPerRay).min(screenX)) {
            wallBounds(px) = (screenY / 2, screenY / 2)
          }
      }
    }


    val posZ = 0.5 * screenY
    for (i <- game.rays.indices) {
      val ray = game.rays(i)
      val rayPosX = i * game.pixelsPerRay
      val (wallTop, wallBottom) = wallBounds(rayPosX)

      val rayAngle = player.dir - game.fov / 2.0 + i * game.rayAngleStep
      val rayDirX = math.cos(rayAngle)
      val rayDirY = math.sin(rayAngle)

      var y = wallBottom
      while (y < screenY) {// piirretään lattia
        val p = y - screenY / 2.0
        if (p > 0.0) {
          val rowDistance = posZ / p
          val floorX = player.x + rayDirX * rowDistance
          val floorY = player.y + rayDirY * rowDistance

          val cellX = floorX.toInt
          val cellY = floorY.toInt

          if (cellX >= 0 && cellX < game.map.size && cellY >= 0 && cellY < game.map.size) {
            val fx = ((floorX - math.floor(floorX)) * texSize).toInt
            val fy = ((floorY - math.floor(floorY)) * texSize).toInt
            val tx = math.min(math.max(fx, 0), texSize - 1)
            val ty = math.min(math.max(fy, 0), texSize - 1)

            val floorTexId = game.map.grid(0)(cellY)(cellX)
            if (floorTexId != 0) {
              val floorColor = TextureManager.getTexturePixel(floorTexId, tx, ty)
              bg.setColor(floorColor)
              bg.fillRect(rayPosX, y, game.pixelsPerRay, 1)
            }
          }
        }
        y += 1
      }

      y = 0
      while (y < wallTop) { // piirretään katto
        val p = (screenY / 2.0) - y
        if (p > 0.0) {
          val rowDistance = posZ / p
          val ceilX = player.x + rayDirX * rowDistance
          val ceilY = player.y + rayDirY * rowDistance

          val cellX = ceilX.toInt
          val cellY = ceilY.toInt

          if (cellX >= 0 && cellX < game.map.size && cellY >= 0 && cellY < game.map.size) {
            val fx = ((ceilX - math.floor(ceilX)) * texSize).toInt
            val fy = ((ceilY - math.floor(ceilY)) * texSize).toInt
            val tx = math.min(math.max(fx, 0), texSize - 1)
            val ty = math.min(math.max(fy, 0), texSize - 1)

            val ceilTexId = game.map.grid(2)(cellY)(cellX)
            if (ceilTexId != 0) {
              val ceilColor = TextureManager.getTexturePixel(ceilTexId, tx, ty)
              bg.setColor(ceilColor)
              bg.fillRect(rayPosX, y, game.pixelsPerRay, 1)
            }
          }
        }
        y += 1
      }
    }
    val zBuffer = new Array[Double](screenX) // Spritejen renderöintii varten otetaan talteen jokasen rayn etäisyys

    // Eli. Toi ray ottaa jokasen osuman listaan muistiin kunnes se löytää jonku läpinäkymättömän seinän.
    // Nää kaikki osumat on järjestyksessä missä lähin osuma on ekana ja kaukasin osuma vikana.
    // Pirretään jokasen rayn jokanen osuma kaukasimmasta lähimpään.
    for (i <- game.rays.indices) {
      val rayColumn = game.rays(i)
      val rayPosX = i * game.pixelsPerRay

      for (hit <- rayColumn.hits.reverse) {
        val rayHeight = ((screenY / hit.fixedDistance)).toInt
        val rayTopY = (screenY / 2) - (rayHeight / 2)

        val texX = (hit.texX * texSize).toInt
        val texId = hit.texId
        val texSlice = textureCache.getOrElseUpdate((texId, texX), TextureManager.getTexture(texId, texX))

        val isOpaque = game.map.isOpaque(texId)
        if (!isOpaque) {
          bg.drawImage(texSlice, rayPosX, rayTopY, game.pixelsPerRay, rayHeight, this)
        } else {
          bg.drawImage(texSlice, rayPosX, rayTopY, game.pixelsPerRay, rayHeight, this)
        }
      }
    }

    val sortedSprites = game.sprites.sortBy(s =>
      -math.pow(s.x - player.x, 2) - math.pow(s.y - player.y, 2)
    )
    bg.dispose()
    g.drawImage(buffer, 0, 0, this)

  }
}
