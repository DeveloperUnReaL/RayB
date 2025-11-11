package core
import javax.swing.*

class Game(val map: Map, val player: Player) {
  val keyHandler = new KeyHandler(player)
  var frameCount: Int = 0

  private var views: Vector[javax.swing.JPanel] = Vector()
  private var running: Boolean = false


  val pixelsPerRay = 4
  val screenX = 800
  val rayAmount = screenX / pixelsPerRay
  val fov = math.toRadians(60.0)
  val rayAngleStep = fov/rayAmount

  var rays: Array[RayColumn] = Array.empty

  def addView(view: JPanel) = views :+= view

  def start(): Unit = {
    running = true
    var lastTime = System.nanoTime()
    val fps = 30
    val frameTime = 1e9 / fps

    new Thread(() =>
      while running do
        val now = System.nanoTime()
        val delta = (now - lastTime) / 1e9 // Time between frames
        lastTime = now

        render()
        update(delta)
        //println(fps)

        val sleep = ((frameTime - (System.nanoTime() - now)) / 1e6).toLong
        if sleep > 0 then Thread.sleep(sleep)
    ).start()
  }

  def update(delta: Double): Unit = {
    player.update(delta, map)
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
