package view

import scala.collection.immutable.Map as ScalaMap
import javax.swing.*
import java.awt.*
import core.*

class View3D(game: Game) extends JPanel{
  private val frame = new JFrame("RayBlaster3D")
  frame.addKeyListener(game.keyHandler)
  frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE)

  val screenX = game.screenX
  val screenY = 600
  val texSize = TextureManager.tileSize

  private val textureCache = scala.collection.mutable.Map[(Int, Int), Image]()

  frame.setSize(screenX, screenY)
  frame.add(this)
  frame.setVisible(true)

  override def paintComponent(g: Graphics): Unit = {
    super.paintComponent(g)

    val player = game.player

    // background
    g.setColor(Color.CYAN); g.fillRect(0, 0, screenX, screenY)

    val dirX = math.cos(player.dir)
    val dirY = math.sin(player.dir)
    val planeLen = math.tan(game.fov / 2.0) // fov in rad
    val planeX = -dirY * planeLen
    val planeY = dirX * planeLen

    val wallBounds = Array.ofDim[(Int, Int)](screenX)
    for (i <- game.rays.indices) {
      val ray = game.rays(i)
      val rayHeight = ((screenY / ray.fixedDistance)).toInt
      val rayPosX = i * game.pixelsPerRay
      val rayTopY = (screenY / 2) - (rayHeight / 2)
      val rayBottomY = rayTopY + rayHeight
      for (px <- rayPosX until (rayPosX + game.pixelsPerRay).min(screenX)) {
        wallBounds(px) = (rayTopY, rayBottomY)
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
      while (y < screenY) { /// ******* CEILING AND FLOOR
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
              g.setColor(floorColor)
              g.fillRect(rayPosX, y, game.pixelsPerRay, 1)
            }
          }
        }
        y += 1
      }

      y = 0
      while (y < wallTop) {
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
              g.setColor(ceilColor)
              g.fillRect(rayPosX, y, game.pixelsPerRay, 1)
            }
          }
        }
        y += 1
      }
    }

    // draw walls
    for (i <- game.rays.indices) {
      val ray = game.rays(i)
      val rayHeight = ((screenY / ray.fixedDistance)).toInt
      val rayPosX = i * game.pixelsPerRay
      val rayTopY = (screenY / 2) - (rayHeight / 2)

      val texX = (ray.texX * texSize).toInt
      val texId = ray.texId

      val texSlice = textureCache.getOrElseUpdate((texId, texX), TextureManager.getTexture(texId, texX))

      val dimness = (2.0f / (1.0f + ray.realDistance.toFloat))
      val alpha = Math.min((dimness * 255).toInt, 255)

      g.drawImage(texSlice, rayPosX, rayTopY, game.pixelsPerRay, rayHeight, this)
      g.setColor(new Color(0, 0, 0, 255 - alpha))
      g.fillRect(rayPosX, rayTopY, game.pixelsPerRay, rayHeight)
    }
  }
}