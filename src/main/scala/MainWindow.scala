import core.*
import view.*


@main def runRayBlaster(): Unit = {
  val map = new Map()
  val player = new Player(map.spawnPos._2, map.spawnPos._1)
  val game = new Game(map, player)

  val view2D = new View2D(game)
  val view3D = new View3D(game)

  game.addView(view2D)
  game.addView(view3D)

  game.start()
}
