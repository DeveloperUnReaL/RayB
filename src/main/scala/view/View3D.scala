package view

import scala.collection.immutable.Map as ScalaMap
import javax.swing.*
import java.awt.*
import core.*

class View3D(game: Game) extends JPanel{
  private val frame = new JFrame("RayBlaster3D")
  frame.addKeyListener(game.keyHandler)
  frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE)

  val screenX = game.screenX //Hack fix, remove later
  val screenY = 600

  val colorMap: ScalaMap[String, Color] = ScalaMap(
    "ground" -> Color.GRAY,
    "sky" -> Color.CYAN,
    "wall" -> Color.GREEN,
  )

  private val textureCache = scala.collection.mutable.Map[(Int, Int), Image]()

  frame.setSize(screenX, screenY)
  frame.add(this)
  frame.setVisible(true)

  override def paintComponent(g: Graphics): Unit = {
    super.paintComponent(g)

    // Draw the sky
    g.setColor(colorMap("sky"))
    g.fillRect(0, 0, screenX, screenY)

    // DRAW FLOOR, basically just paint the lower half of the screen, floor rendering?
    g.setColor(colorMap("ground"))
    g.fillRect(0, screenY / 2, screenX, screenY / 2)

    // DRAW WALLS
    val texSize = TextureManager.tileSize
    var i: Int  = 0

    for (ray <- game.rays) {
      val rayHeight = ((screenY / (ray.fixedDistance))).toInt
      val rayPosX: Int = i * game.pixelsPerRay
      val rayPosY: Int = (screenY / 2) - (rayHeight / 2)
      val texX = (ray.texX * texSize).toInt
      val texId = 1

      val texSlice = textureCache.getOrElseUpdate((texId, texX), {
        TextureManager.getTexture(texId, texX)
      })

      val dimness = (2.0f / (1.0f + ray.realDistance.toFloat * 1))
      val alpha = Math.min((dimness * 255).toInt, 255)
      val shading = new Color(alpha, alpha, alpha)

      //g.setColor(Color.getHSBColor(0.36, 1, dimness))
      //g.fillRect(rayPosX, rayPosY, game.pixelsPerRay, rayHeight)
      g.drawImage(texSlice, rayPosX, rayPosY, game.pixelsPerRay, rayHeight, this)

      g.setColor(new Color(0, 0, 0, (255 - alpha)))
      g.fillRect(rayPosX, rayPosY, game.pixelsPerRay, rayHeight)
      i += 1
    }
  }
}