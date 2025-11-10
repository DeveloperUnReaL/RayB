package view

import javax.swing.*
import java.awt.*
import core.*


class View2D(game: Game) extends JPanel{
  private val frame = new JFrame("RayBlaster2D")
  frame.addKeyListener(game.keyHandler)

  val screenSize = 600

  frame.setSize(screenSize, screenSize) //Always square :D
  frame.add(this)
  frame.setVisible(true)


  override def paintComponent(g: Graphics): Unit = {
    super.paintComponent(g)
    ///tosiaan pyyhitään näyttö ennen seuraavaa framea
    g.setColor(Color.GRAY)
    g.fillRect(0, 0, getWidth, getHeight)

    val map = this.game.map
    val gridSize: Int = map.size
    val tileSize = this.screenSize / gridSize
    val player = game.player


    /// DRAW MAP
    for y <- map.grid(1).indices; x <- map.grid(1)(y).indices do {
      if map.grid(1)(y)(x) == 0 then
        g.setColor(Color.WHITE)
      if map.grid(1)(y)(x) == 1 then
        g.setColor(Color.BLACK)
      g.fillRect(x * tileSize, y * tileSize, tileSize - 1, tileSize - 1)
    }


    // DRAW RAYS
    g.setColor(Color.GREEN)
    val px = (player.x * tileSize).toInt
    val py = (player.y * tileSize).toInt
    for (ray <- game.rays) {
      g.drawLine(px, py, (ray.x * tileSize).toInt, (ray.y * tileSize).toInt)
    }


    /// DRAW PLAYER
    g.setColor(Color.BLUE)
    g.fillOval((player.x * tileSize - 10).toInt, (player.y * tileSize - 10).toInt, 20, 20)

    // suuntanuoli
    g.setColor(Color.RED)
    g.drawLine(px, py, ((player.x + math.cos(player.dir) * 0.5) * tileSize).toInt, ((player.y + math.sin(player.dir) * 0.5) * tileSize).toInt)
  }
}