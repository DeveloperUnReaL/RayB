package core.entities

case class SpriteObject(
    var x: Double,
    var y: Double,
    override val texId: Int,
    override val hitRadius: Double = 0
) extends Sprite {
  override def update(delta: Double): Unit = {}
  override def takeDamage(amount: Int): Unit = {}
}
