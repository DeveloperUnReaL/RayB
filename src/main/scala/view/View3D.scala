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

  frame.setSize(screenX + 16, screenY + 38)
  frame.add(this)
  frame.setVisible(true)

  def normalizeAngle(a: Double): Double = {
    var ang = a
    while (ang <= -math.Pi) ang += 2 * math.Pi
    while (ang > math.Pi) ang -= 2 * math.Pi
    ang
  }

  private def drawHUD(g: Graphics2D, player: Player): Unit = {
    val cx = screenX / 2
    val cy = screenY / 2

    val gunScale = 8

    val crosshairSize = 10
    val crosshairThickness = 2
    val crosshairColor = Color.WHITE

    g.setColor(Color.RED)
    g.fillRect(20, 50, (200 * player.healthPercent).toInt, 25)
    g.setColor(Color.WHITE)
    g.drawRect(20, 50, 200, 25)

    g.setFont(new Font("Arial", Font.PLAIN, 25))
    g.drawString(s"Score: ${player.score}", 100, (screenY/1.10).toInt)

    g.drawImage(TextureManager.getSprite(player.hudSpriteId), (cx - 32 * gunScale), screenY - 64 * gunScale, 64 * gunScale, 64 * gunScale, this)

    g.setColor(crosshairColor)
    g.fillRect(cx - crosshairSize, cy - crosshairThickness + 5, crosshairSize * 2, crosshairThickness)
    g.fillRect(cx - crosshairThickness + 1, cy - crosshairSize + 4, crosshairThickness, crosshairSize * 2)

    g.setColor(Color.WHITE)
    g.setFont(new Font("Arial", Font.PLAIN, 18))
    g.drawString(player.hintText, cx - 25, cy + 30)

    g.drawString(player.storyText, cx - 200, screenY - 180)
  }

  override def paintComponent(g: Graphics): Unit = { ///HUHHUHHUH. En ois uskonu et joudun kirjottaa sekä buffering että two pass rendering algoritmin :DD
    super.paintComponent(g)

    val bg = buffer.getGraphics.asInstanceOf[Graphics2D]

    val player = game.player

    // bg
    bg.setColor(Color.getHSBColor(0, 0.05, 0.10))
    bg.fillRect(0, 0, screenX, screenY)

    if player.dead then {
      bg.setColor(new Color(0.1f, 0.1f, 0.1f, 0.8f))
      bg.fillRect(0, 0, screenX, screenY)
      bg.setColor(Color.RED)
      bg.setFont(new Font("Arial", Font.BOLD, 48))
      bg.drawString("YOU DIED", screenX/2 - 120, screenY/2)
      bg.setFont(new Font("Arial", Font.PLAIN, 28))
      bg.drawString(s"Score: ${player.score}", screenX/2 - 120, (screenY/1.5).toInt)
      g.drawImage(buffer, 0, 0, this)
      return
    }

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
    //val zBuffer = new Array[Double](screenX) // Spritejen renderöintii varten otetaan talteen jokasen rayn etäisyys
    val zBuffer = Array.fill[Double](screenX)(Double.PositiveInfinity)

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

        bg.drawImage(texSlice, rayPosX, rayTopY, game.pixelsPerRay, rayHeight, this)

        for (px <- rayPosX until (rayPosX + game.pixelsPerRay).min(screenX)) {
          zBuffer(px) = math.min(zBuffer(px), hit.fixedDistance)
        }
      }
    }

    val sortedSprites = game.sprites.sortBy(s => -(s.x - player.x)*(s.x - player.x) - (s.y - player.y)*(s.y - player.y))

    for sprite <- sortedSprites do {
      val dx = sprite.x - player.x
      val dy = sprite.y - player.y

      val angleToSprite = math.atan2(dy, dx)
      val relativeAngle = normalizeAngle(angleToSprite - player.dir)

      if (math.abs(relativeAngle) < game.fov / 1.5) { /// jos sprite on näkyvil
        val spriteScreenX = ((relativeAngle + game.fov / 2) / game.fov) * screenX // Spriten pixel positio näytöllä (x)

        val dist = math.sqrt(dx*dx + dy*dy)
        val perpDist = dist * math.cos(relativeAngle) // ettei tuu fisheye

        val spriteSize = (screenY / perpDist).toInt

        val spriteTopY = (screenY / 2) - (spriteSize / 2)
        val spriteLeftX = spriteScreenX.toInt - (spriteSize / 2) // Eli piirretään sprite spriteLeftX->spriteLeftX+spritesize asti

        for x <- spriteLeftX until spriteLeftX + spriteSize do {
          if x >= 0 && x < screenX && perpDist < zBuffer(x) then {
            val texX = ((x - spriteLeftX) * TextureManager.spriteSize / spriteSize)

            for y <- spriteTopY until spriteTopY + spriteSize do {
              val texY = ((y - spriteTopY) * TextureManager.spriteSize / spriteSize)
              val color = TextureManager.getSpritePixel(sprite.texId, texX, texY)
              if (color.getAlpha != 0) {
                bg.setColor(color)
                bg.drawLine(x, y, x, y)
              }
            }
          }
        }
      }
    }

    bg.setColor(new Color(0.0f, 0.0f, 0.0f, 0.4f))
    bg.fillRect(0, 0, screenX, screenY)

    bg.dispose()
    g.drawImage(buffer, 0, 0, this)

    drawHUD(g.asInstanceOf[Graphics2D], player)

  }
}
