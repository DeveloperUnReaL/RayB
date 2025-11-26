package core.entities

import core.Game

class Orb(
          var x: Double,
          var y: Double,
          val game: Game,
          override val texId: Int = 27,
          val damage: Int = 30,
          var speed: Double = 4.5,
          override val hitRadius: Double = 0.2,
          val direction: Double = 0.0,
          var dead: Boolean = false,
         )
  extends Sprite{

  val map = game.map

  def update(delta: Double): Unit = {
    move(delta, direction)
    takeDamage(0)
  }

  def move(delta: Double, dir: Double): Unit = {
    x = x + (math.cos(dir) * speed * delta)
    y = y + (math.sin(dir) * speed * delta)
  }

  def takeDamage(amount: Int): Unit = { // katotaa et ollaanks seinän sisällä, jos osutaan seinään ni me kuollaan
    if (!map.notSolid(x,y,1)) then dead = true
  }
}
