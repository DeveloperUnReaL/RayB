package view

import javax.swing.*
import java.awt.*
import core.*
import core.entities.*


class View2D(game: Game) extends JPanel{
  private val frame = new JFrame("RayBlaster2D")
  frame.addKeyListener(game.keyHandler)

  val screenSize = 600

  frame.setSize(screenSize + 16, screenSize + 38) //Always square :D
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
        if map.isSpawnable(x,y) then g.setColor(Color.CYAN) else g.setColor(Color.white) // mihin spawneri voi spawnata
      if map.grid(1)(y)(x) == 1 then
        g.setColor(Color.BLACK)
      if map.grid(1)(y)(x) == 6 then // suljettu ovi
        g.setColor(Color.BLUE)
      if map.grid(1)(y)(x) == 7 then // avoin ovi
        g.setColor(Color.GREEN)
      g.fillRect(x * tileSize, y * tileSize, tileSize - 1, tileSize - 1)
      g.setColor(Color.PINK)
      g.drawString(s"$x - $y", x * tileSize, y * tileSize)
    }


    // DRAW RAYS
    g.setColor(new Color(0.0f, 1f, 0.0f, 0.1f))
    val px = (player.x * tileSize).toInt
    val py = (player.y * tileSize).toInt
    for (rayColumn <- game.rays) {
      rayColumn.firstOpaqueHit(map).foreach( hit => g.drawLine(px, py, (hit.x * tileSize).toInt, (hit.y * tileSize).toInt))
    }

    /// DRAW SPRITES
    for (sprite <- game.sprites) {
      sprite match
        case s: Spawner    => g.setColor(Color.RED)
        case e: Enemy      => g.setColor(Color.ORANGE)
        case b: BossEnemy  => g.setColor(Color.RED)
        case o: Orb        => g.setColor(Color.magenta)
        case h: HealthBag  => g.setColor(Color.GREEN)
        case _             => g.setColor(Color.GRAY)

      g.fillOval(
        (sprite.x * tileSize - 10).toInt,
        (sprite.y * tileSize - 10).toInt,
        20,
        20
      )
    }
    /// DRAW PLAYER
    g.setColor(Color.BLUE)
    g.fillOval((player.x * tileSize - 10).toInt, (player.y * tileSize - 10).toInt, 20, 20)

    // suuntanuoli
    g.setColor(Color.RED)
    g.drawLine(px, py, ((player.x + math.cos(player.dir) * 0.5) * tileSize).toInt, ((player.y + math.sin(player.dir) * 0.5) * tileSize).toInt)
  }
}