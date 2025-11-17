package core
import javax.swing.*

class Game(val map: Map, val player: Player) {
  val keyHandler = new KeyHandler(player)
  var frameCount: Int = 0

  private var views: Vector[javax.swing.JPanel] = Vector()
  private var running: Boolean = false


  val pixelsPerRay = 2
  val screenX = 800
  val rayAmount = screenX / pixelsPerRay
  val fov = math.toRadians(60.0)
  val rayAngleStep = fov/rayAmount

  var rays: Array[RayColumn] = Array.empty

  val pillar = SpriteObject(10, 9.5, 1, 0.5)
  val plant = SpriteObject(11, 9.5, 2)
  val table = SpriteObject(12, 9.5, 3)
  var sprites = Vector(pillar, plant, table)

  def addView(view: JPanel) = views :+= view

  def addSprite(sprite: SpriteObject) = sprites :+ SpriteObject

  def start(): Unit = {
    running = true
    var lastTime = System.nanoTime()
    val fps = 60
    val frameTime = 1e9 / fps

    new Thread(() =>
      while running do
        val now = System.nanoTime()
        val delta = (now - lastTime) / 1e9 // Time between frames
        lastTime = now

        render()
        update(delta)

        val sleep = ((frameTime - (System.nanoTime() - now)) / 1e6).toLong
        if sleep > 0 then Thread.sleep(sleep)
    ).start()
  }

  def update(delta: Double): Unit = {
    player.update(delta, map)
    for (spriteObject <- sprites) {spriteObject.update()}
    castAllRays()
  }

  def castAllRays(): Unit = {
    rays = Array.tabulate(rayAmount) { i =>
      val rayAngle = player.dir - fov / 2.0 + i * rayAngleStep
      RayCaster.castRay(player, rayAngle, map, renderLayer = 1)
    }
  }

  def render(): Unit =
    frameCount += 1
    //println("uus frame!: " + frameCount)
    for view <- views do view.repaint()
}
