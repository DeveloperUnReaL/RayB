package core

case class SpriteObject(x: Double, y: Double, texId: Int, hitRadius: Double = 0) {
  def update(): Unit = {}
}
