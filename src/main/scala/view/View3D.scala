package view

import javax.swing.*
import java.awt.*
import core.*

class View3D(game: Game) extends JPanel{
  private val frame = new JFrame("RayBlaster3D")

  frame.addKeyListener(game.keyHandler)

  val rayAmount = game.player.rayAmount

  val screenX = rayAmount * 2
  val screenY = 800

  frame.setSize(screenX, screenY)
  frame.add(this)
  frame.setVisible(true)

  override def paintComponent(g: Graphics): Unit = {
    println("here")
    super.paintComponents(g)

    g.setColor(Color.BLUE)
    g.fillRect(0, 0, getWidth, getHeight)
    g.setColor(Color.WHITE)
    g.drawString("3D View placeholder", 350, 300)
  }
}