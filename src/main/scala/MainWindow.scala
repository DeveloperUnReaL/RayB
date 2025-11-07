import core.*
import view.*

import javax.swing.*
import java.awt.*
import java.awt.event.{KeyAdapter, KeyEvent}

@main def runRayBlaster(): Unit = {
  val map = new Map()
  val player = new Player(map.spawnPos._1, map.spawnPos._2)
  val game = new Game(map, player)

  val view2D = new View2D(game)
  //val view3D = new View3D(game)

  game.addView(view2D)

  game.start()
}
