package view

import javax.swing.*
import java.awt.*
import core.*



class View2D(game: Game) extends JPanel{
  private val frame = new JFrame("RayBlaster2D")

  frame.addKeyListener(game.keyHandler)

  val rayAmount = game.player.rayAmount
  val fov = game.player.fov
  val rayAngleStep = game.player.rayAngleStep

  val screenSize = 600

  frame.setSize(screenSize, screenSize) //Always square :D
  frame.add(this)
  frame.setVisible(true)

  def dist(ax: Double, ay: Double, bx: Double, by: Double, dir: Double) = ( math.sqrt((bx-ax)*(bx-ax) + (by-ay)*(by-ay)) )

  override def paintComponent(g: Graphics): Unit = {
    super.paintComponent(g)

    ///tosiaan pyyhitään näyttö ennen seuraavaa framea
    g.setColor(Color.GRAY)
    g.fillRect(0, 0, getWidth, getHeight)

    /// CONSTANTS:
    val Pi2 = math.Pi / 2
    val Pi3 = (math.Pi * 3) / 2

    val map = this.game.map
    val gridSize: Int = map.size
    val tileSize = this.screenSize / gridSize
    val player = game.player

    for y <- map.grid.indices; x <- map.grid(y).indices do {
      if map.grid(y)(x) == 0 then
        g.setColor(Color.WHITE)
      if map.grid(y)(x) == 1 then
        g.setColor(Color.BLACK)
      g.fillRect(x * tileSize, y * tileSize, tileSize - 1, tileSize - 1)
    }
    g.setColor(Color.BLUE)

    g.fillOval((player.x * tileSize - 10).toInt, (player.y * tileSize - 10).toInt, 20, 20)

    //Debug rays
    g.setColor(Color.GREEN)

    var mx: Int = 0 // Tiles
    var my: Int = 0 // Tiles
    var mp: Int = 0 // Tiles
    var dof: Int = 0 // Ray max distance //Tiles

    var rx: Double = 0 //Ray x //pixels
    var ry: Double = 0 //Ray y //pixels
    var ra: Double = 0 //Ray angle
    var xo: Double = 0 //x offset //pixels
    var yo: Double = 0 //y offset //pixels
    for (ray <- 0 to this.rayAmount) {
      ra = player.dir - this.fov / 2 + ray * rayAngleStep
      val aTan = -1 / math.tan(ra)
      val Tan = if (math.tan(ra) == 0) 1e9 else -1.0 / math.tan(ra) ////??????
      //val nTan = if (math.tan(ra) == 0) 1e9 else 1.0 / math.tan(ra) ////??????


      //   -----  Horizontal  -----
      dof = 0
      var distH: Double = 1000000
      var hx = player.x
      var hy = player.y
      if ra < math.Pi then { // Looking down
        ry = math.floor(player.y) + 1.0
        rx = (player.y - ry) * Tan + player.x
        yo = 1.0
        xo = -yo * Tan
      }
      if ra > math.Pi then { // Looking up
        ry = math.floor(player.y) - 1e-6
        rx = (player.y - ry) * Tan + player.x
        yo = -1.0
        xo = -yo * Tan
      }
      if ra == 0 || ra == math.Pi then {
        rx = player.x
        ry = player.y
        dof = 5
      }
      while (dof < 5) {
        mx = rx.toInt
        my = ry.toInt
        mp = my * map.size + mx
        //println(mx + " -- " + my + " -- " + rx + " -- " + ry + " -- ")
        if (my >= 0 && my < gridSize && mx >= 0 && mx < gridSize) && (map.grid(my)(mx) == 1) then {
          hx = rx
          hy = ry
          distH = dist(player.x, player.y, hx, hy, ra)
          dof = 8
        }
        else {
          rx  += xo
          ry  += yo
          dof += 1
        }
      }



      //     -----  Vertical  -----
      dof = 0
      var distV: Double = 1000000
      var vx = player.x
      var vy = player.y
      if ra > Pi2 && ra < Pi3 then { // Looking left
        rx = math.floor(player.x) - 1e-6
        ry = (player.x - rx) * (-math.tan(ra)) + player.y
        xo = -1.0
        yo = -xo * (-math.tan(ra))
      }
      if ra < Pi2 || ra > Pi3 then { // Looking right
        rx = math.floor(player.x) + 1.0
        ry = (player.x - rx) * (-math.tan(ra)) + player.y
        xo = 1.0
        yo = -xo * (-math.tan(ra))
      }
      if ra == Pi2 || ra == Pi3 then {
        rx = player.x
        ry = player.y
        dof = 5
      }
      while (dof < 5) {
        mx = rx.toInt
        my = ry.toInt
        mp = my * map.size + mx
        //println(mx + " -- " + my + " -- " + rx + " -- " + ry + " -- ")
        if (my >= 0 && my < gridSize && mx >= 0 && mx < gridSize) && (map.grid(my)(mx) == 1) then {
          vx = rx
          vy = ry
          distV = dist(player.x, player.y, vx, vy, ra)
          dof = 8
        }
        else {
          rx  += xo
          ry  += yo
          dof += 1
        }
      }
      /// Draw the shortes ray
      if distV < distH then {rx = vx; ry = vy}
      if distH < distV then {rx = hx; ry = hy}
      g.drawLine((player.x * tileSize).toInt, (player.y * tileSize).toInt, (rx * tileSize).toInt, (ry * tileSize).toInt)
    }

    // Suuntanuoli
    g.setColor(Color.RED)
    g.drawLine((player.x * tileSize).toInt, (player.y * tileSize).toInt, ((player.x + math.cos(player.dir)/2) * tileSize).toInt, ((player.y + math.sin(player.dir)/2) * tileSize).toInt)
  }
}