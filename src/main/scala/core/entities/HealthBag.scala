package core.entities

import core.Game

case class HealthBag(
          var x: Double,
          var y: Double,
          val game: Game,
          var healAmount: Int = 20, /// HEALING EFFECT :D
          override val texId: Int = 28,
          override val hitRadius: Double = 0.2,
          var dead: Boolean = false, // consumed :D

) extends Sprite {
  override def update(delta: Double): Unit = {
    val dx = game.player.x - x
    val dy = game.player.y - y
    val distance = math.sqrt(dx*dx + dy*dy)

    if (distance <= hitRadius) {
      println("heal")
      dead = true
      game.player.heal(healAmount)
    } /// laitetaan pelaajalle se hp, vähä hack fix kyl mut 3h aikaa


  }
  override def takeDamage(amount: Int): Unit = {}
}
