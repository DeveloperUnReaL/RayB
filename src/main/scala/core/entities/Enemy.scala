package core.entities
import core.*

class Enemy(
  var x: Double,
  var y: Double,
  override val texId: Int = 4,
  var hp: Int = 30,
  val damage: Int = 34,
  val speed: Double = 0,
  override val hitRadius: Double = 0.2,
  val score: Int = 1
) extends Sprite {
  val hitFlashDuration: Double = 0.35
  var hitFlashTimer: Double = 0.0
  var hitFlash: Boolean = false
  override def update(delta: Double): Unit = {
    x += speed * delta
    hitFlashTimer -= delta
    if (hitFlashTimer <= 0) hitFlash = false
  }

  override def takeDamage(amount: Int): Unit = {
    if (!hitFlash) {
      hp -= amount
      hitFlash = true
      hitFlashTimer = hitFlashDuration
      if (hp <= 0) die()
    }
  }

  def die(): Unit = println("enemy died!")
}
