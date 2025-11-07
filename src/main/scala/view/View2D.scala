package view

import javax.swing.*
import java.awt.*
import core.*



class View2D(game: Game) extends JPanel{
  private val frame = new JFrame("RayBlaster2D")
  frame.addKeyListener(game.keyHandler)

  val screenSize = 600
  val rayAmount = game.player.rayAmount
  val fov = game.player.fov
  val rayAngleStep = game.player.rayAngleStep


  frame.setSize(screenSize, screenSize) //Always square :D
  frame.add(this)
  frame.setVisible(true)

  val PI = math.Pi
  val PI2 = math.Pi / 2
  val PI3 = (math.Pi * 3) / 2
  val PI4 = math.Pi * 2

  private def normalizeAngle(angle: Double): Double = {
    var nAngle = angle % PI4
    if nAngle < 0 then nAngle += PI4
    nAngle
  }

  private def dist(ax: Double, ay: Double, bx: Double, by: Double) = ( math.sqrt((bx-ax)*(bx-ax) + (by-ay)*(by-ay)) )


  private def castRay(player: Player, angleIn: Double, map: Map, maxStep: Int = 1024): (Double, Double, Double) = {
    val ang = normalizeAngle(angleIn)
    val dx = math.cos(ang)
    val dy = math.sin(ang)

    // Players map square
    var mapX = player.x.toInt
    var mapY = player.y.toInt

    val deltaDistX = if dx == 0.0 then Double.PositiveInfinity else math.abs(1.0 / dx)
    val deltaDistY = if dy == 0.0 then Double.PositiveInfinity else math.abs(1.0 / dy)

    val stepX = if dx < 0 then -1 else 1
    val stepY = if dy < 0 then -1 else 1

    var sideDistX = {
      if dx > 0 then (mapX + 1.0 - player.x) * deltaDistX
      else (player.x - mapX) * deltaDistX
    }
    var sideDistY = {
      if dy > 0 then (mapY + 1.0 - player.y) * deltaDistY
      else (player.y - mapY) * deltaDistY
    }

    var hit = false
    var side = 0 // 0 -> Vertical hit (X-side), 1 -> Horizontal hit (Y-side)
    var steps = 0

    while (!hit && steps < maxStep) {
      if sideDistX < sideDistY then {
        sideDistX += deltaDistX
        mapX += stepX
        side = 0
      } else {
        sideDistY += deltaDistY
        mapY += stepY
        side = 1
      }

      if ((mapY >= 0) && (mapY < map.size) && (mapX >= 0) && (mapX < map.size) && (map.grid(mapY)(mapX) == 1)) hit = true
      steps += 1
    }

    if (!hit) {
      val far = 1000.0
      val hx = player.x + dx * far
      val hy = player.y + dy * far
      val dist = this.dist(hx, hy, player.x, player.y)
      (hy, hx, dist)
    } else {
      val dist = if (side == 0) {
        (mapX - player.x + (1 - stepX) / 2.0) / dx
      } else {
        ((mapY - player.y + (1 - stepY) / 2.0) / dy).abs
      }
      val hitX = player.x + dx * dist
      val hitY = player.y + dy * dist

      (hitX, hitY, dist)
    }
  }


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
    for y <- map.grid.indices; x <- map.grid(y).indices do {
      if map.grid(y)(x) == 0 then
        g.setColor(Color.WHITE)
      if map.grid(y)(x) == 1 then
        g.setColor(Color.BLACK)
      g.fillRect(x * tileSize, y * tileSize, tileSize - 1, tileSize - 1)
    }

    /// DRAW PLAYER
    g.setColor(Color.BLUE)
    g.fillOval((player.x * tileSize - 10).toInt, (player.y * tileSize - 10).toInt, 20, 20)

    //Debug rays
    g.setColor(Color.GREEN)
    val px = (player.x * tileSize).toInt
    val py = (player.y * tileSize).toInt

    for (i <- 0 until rayAmount) {
      val rayAngle = player.dir - fov / 2.0 + i * rayAngleStep
      val (hx, hy, dist) = castRay(player, rayAngle, map)
      g.setColor(Color.GREEN)
      g.drawLine(px, py, (hx * tileSize).toInt, (hy * tileSize).toInt)
    }

    // suuntanuoli
    g.setColor(Color.RED)
    g.drawLine(px, py, ((player.x + math.cos(player.dir) * 0.5) * tileSize).toInt, ((player.y + math.sin(player.dir) * 0.5) * tileSize).toInt)
  }
}