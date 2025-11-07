package core
import javax.swing.*
import java.awt.*

class Game(val map: Map, val player: Player) {
  val keyHandler = new KeyHandler(player)
  var frameCount: Int = 0

  private var views: Vector[javax.swing.JPanel] = Vector()
  private var running: Boolean = false

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

        val sleep = ((frameTime - (System.nanoTime() - now)) / 1e6).toLong
        if sleep > 0 then Thread.sleep(sleep)
    ).start()
  }

  def update(delta: Double): Unit = player.update(delta, map)

  def render(): Unit =
    frameCount += 1
    //println("uus frame!: " + frameCount)
    for view <- views do view.repaint()
}
