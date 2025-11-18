package core
import core.entities.*
import javax.swing.*

class Game(val map: Map, val player: Player) {
  val keyHandler = new KeyHandler(player)
  var frameCount: Int = 0
  var gameState: Int = 0
  var bossSpawned: Boolean = false
  var bossKilled: Boolean = false
  val spawnerCount: Int = 5
  val minSpawnDist: Double = 6

  private var views: Vector[javax.swing.JPanel] = Vector()
  private var running: Boolean = false


  val pixelsPerRay = 2
  val screenX = 800
  val rayAmount = screenX / pixelsPerRay
  val fov = math.toRadians(60.0)
  val rayAngleStep = fov/rayAmount

  var rays: Array[RayColumn] = Array.empty

  var sprites: Vector[Sprite] = Vector(
    SpriteObject(10, 9.5, 1, 0.1),
    SpriteObject(3.8, 3.2, 2),
    SpriteObject(3.5, 4, 3),
    SpriteObject(1.3, 4.8, 5),
    SpriteObject(1.3, 4.4, 5),
    new Enemy(10, 10, player, map)
  )

  def addView(view: JPanel) = views :+= view

  def spawnEnemy(x: Double, y: Double): Unit = sprites = sprites :+ new Enemy(x, y, player, map)
  def spawnSprite(x: Double, y: Double, texId: Int): Unit = sprites :+= SpriteObject(x, y, texId)

  def spawnSpawner(x: Double, y: Double): Unit = sprites = sprites :+ new Spawner(x, y, this)

  def removeSprite(sprite: Sprite): Unit = {
    sprite match {
      case e: Enemy =>
        player.score += e.score
      case e: Spawner =>
        player.score += e.score
      case _ =>
    }
    sprites = sprites.filterNot(_ eq sprite)
  }

  def start(): Unit = {
    running = true
    var lastTime = System.nanoTime()
    val fps = 144
    val frameTime = 1e9 / fps

    new Thread(() =>
      while running do
        val now = System.nanoTime()
        val delta = (now - lastTime) / 1e9 // Time between frames
        lastTime = now

        checkGameState()

        update(delta)
        castAllRays()
        render()

        val sleep = ((frameTime - (System.nanoTime() - now)) / 1e6).toLong
        if sleep > 0 then Thread.sleep(sleep)
    ).start()
  }

  def update(delta: Double): Unit = {
    player.update(delta, map, this)
    for (spriteObject <- sprites) {spriteObject.update(delta)}
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

  def checkGameState(): Unit = {
    for (sprite <- sprites) { // check for dead enemies
      sprite match {
        case s: Enemy => if s.dead then removeSprite(s)
        case s: Spawner => if s.dead then removeSprite(s)
        case _ => ()
      }
    }

    gameState match {
      case 0 => // game starts after dialog
        if (!(player.activeDialog sameElements player.startText)) gameState = 1

      case 1 => // normal enemies
        if (sprites.forall(!_.isInstanceOf[Spawner])) {
        } else gameState = 2

      case 2 => // bossfight
        for (sprite <- sprites) { // if all spawners are dead, kill all enemies.
          sprite match {
            case s: Enemy => removeSprite(s)
            case _ => ()
          }
        }
        if (!bossSpawned) {
          spawnBoss()
          bossSpawned = true
        } else if (bossKilled) {
          gameState = 3
        }

      case 3 => () // game over
    }
  }

  def spawnBoss(): Unit = {
  }

  def initSpawners(): Unit = {
    val spawnableTiles =
    for {
      y <- 0 until map.size - 1
      x <- 0 until map.size - 1
      if map.isSpawnable(x, y)
    } yield (x, y)

    val randomTiles = util.Random.shuffle(spawnableTiles)

    var picked = Vector.empty[(Int, Int)]

    for ((x, y) <- randomTiles if picked.length < spawnerCount) {
      val tooClose = picked.exists {case (px, py) =>
        val dx = x - px
        val dy = y - py
        math.sqrt(dx*dx + dy*dy) < minSpawnDist
      }
      if !tooClose then picked :+= (x, y)
    }
    for ((x, y) <- picked) spawnSpawner(x + 0.5, y + 0.5)
  }
}
