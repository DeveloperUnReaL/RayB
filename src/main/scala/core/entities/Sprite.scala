package core.entities

trait Sprite {
  var x: Double
  var y: Double
  val texId: Int = 1
  val hitRadius: Double = 0

  def update(delta: Double): Unit
  def takeDamage(amount: Int): Unit
}
