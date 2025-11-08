package view

import scala.collection.immutable.{Map => ScalaMap}
import javax.swing.*
import java.awt.*
import core.*

class View3D(game: Game) extends JPanel{
  private val frame = new JFrame("RayBlaster3D")
  frame.addKeyListener(game.keyHandler)

  val screenX = game.screenX //Hack fix, remove later
  val screenY = 600

  val colorMap: ScalaMap[String, Color] = ScalaMap(
    "ground" -> Color.GRAY,
    "sky" -> Color.CYAN,
    "wall" -> Color.GREEN,
  )

  frame.setSize(screenX, screenY)
  frame.add(this)
  frame.setVisible(true)

  override def paintComponent(g: Graphics): Unit = {
    super.paintComponent(g)

    // Draw the sky
    g.setColor(colorMap("sky"))
    g.fillRect(0, 0, screenX, screenY)

    // DRAW FLOOR, basically just paint the lower half of the screen
    g.setColor(colorMap("ground"))
    g.fillRect(0, screenY / 2, screenX, screenY / 2)

    // DRAW WALLS

    var i: Int  = 0
    for (ray <- game.rays) {
      val rayHeight = ((screenY / (ray.fixedDistance))).toInt
      val dimness = 1.0f / (1.0f + ray.realDistance.toFloat * 0.3f)
      val rayPosX: Int = i * game.pixelsPerRay
      val rayPosY: Int = (screenY / 2) - (rayHeight / 2)
      g.setColor(Color.getHSBColor(0.36, 1, dimness))
      g.fillRect(rayPosX, rayPosY, game.pixelsPerRay, rayHeight)
      i += 1
    }
  }
}