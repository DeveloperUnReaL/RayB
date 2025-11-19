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

  var sprites: Vector[Sprite] = Vector( // täs on vaa dekoraatio
    SpriteObject(10.5, 8.5, 1, 0.1),
    SpriteObject(3.8, 3.2, 2),
    SpriteObject(3.5, 4, 3),
    SpriteObject(1.3, 4.8, 5),
    SpriteObject(1.3, 4.4, 5),
    SpriteObject(2.5, 4.0, 24),
    SpriteObject(6.7, 9.7, 5),
    SpriteObject(6.3, 9.7, 5),
    SpriteObject(5.5, 8.5, 21),
    SpriteObject(11.5, 11.5, 21),
    SpriteObject(7.5, 3.5, 25),
    SpriteObject(5.5, 3, 20),
    SpriteObject(5.2, 1.2, 22),
    SpriteObject(18.5, 1.5, 23),
    SpriteObject(10, 5, 26),
    SpriteObject(4.5, 12.3, 26),
    SpriteObject(15, 15, 1),
    SpriteObject(16, 15, 1),
    SpriteObject(17, 15, 1),
    SpriteObject(18, 15, 1),
    SpriteObject(15, 16, 1),
    SpriteObject(15, 17, 1),
    SpriteObject(15, 18, 1),
    SpriteObject(9.5, 12.5, 25),
    SpriteObject(9, 10, 25),
    SpriteObject(6.5, 16.5, 24),
    SpriteObject(6.5, 11.5, 20),
    SpriteObject(6.3, 15.5, 21),
    SpriteObject(6.4, 16.8, 21),
    SpriteObject(7, 15.8, 21),
    SpriteObject(9.3, 14.3, 22),
    SpriteObject(12.3, 14.3, 22),
    SpriteObject(15, 11, 25),
    SpriteObject(18, 12, 25),
    SpriteObject(16.5, 11.5, 24),
    SpriteObject(8.2, 18.2, 21),
    SpriteObject(7, 18.7, 21),
    SpriteObject(6.4, 18.2, 21),
    SpriteObject(4, 18.5, 21),
    SpriteObject(2, 15, 23),
    SpriteObject(3, 15, 23),
    SpriteObject(2, 16, 23),
    SpriteObject(3, 16, 23),
    SpriteObject(2.3, 15.1, 25),
    SpriteObject(2.9, 14.9, 25),
    SpriteObject(2.1, 15.7, 25),
    SpriteObject(3.1, 16.2, 25),
    //new Enemy(10.5, 8.5, player, map),
  )

  def addView(view: JPanel) = views :+= view

  def spawnEnemy(x: Double, y: Double): Unit = sprites = sprites :+ new Enemy(x, y, player, map)
  def spawnSprite(x: Double, y: Double, texId: Int): Unit = sprites :+= SpriteObject(x, y, texId)

  def spawnSpawner(x: Double, y: Double): Unit = sprites = sprites :+ new Spawner(x, y, this)

  def removeSprite(sprite: Sprite): Unit = {
    println("removed" + sprite)
    sprite match {
      case e: Enemy =>
        player.score += e.score
      case e: Spawner =>
        player.score += e.score
      case e: BossEnemy =>
        player.score += e.score
      case _ =>
    }
    sprites = sprites.filterNot(_ eq sprite)
  }

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
        case s: BossEnemy => if s.dead then bossKilled = true
        case _ => ()
      }
    }
    //println(gameState)
    gameState match {
      case 0 => // game starts after dialog
        if (!(player.activeDialog sameElements player.startText)) gameState = 1

      case 1 => // normal enemies
        if (sprites.exists(_.isInstanceOf[Spawner])) {
        }
        else
          for (sprite <- sprites) { // if all spawners are dead, kill all enemies.
            sprite match {
              case s: Enemy =>
                removeSprite(s)
              case _ => ()
            }
          }
          gameState = 2

      case 2 => // bossfight
        if (!bossSpawned) {
          spawnBoss()
          bossSpawned = true
        } else if (bossKilled) {
          sprites.collectFirst { case b: BossEnemy if b.dead => b} match {
            case Some(deadBoss) =>
              player.score += deadBoss.score
              val (bx, by) = (deadBoss.x, deadBoss.y)
              sprites = sprites.filterNot(_ eq deadBoss)
              sprites = sprites :+ new BossEnemy(bx, by, this, 15, speed = 0, hitRadius = 0, detectionRadius = 0) // ruumis
            case None => ()
          }
          gameState = 3
        }

      case 3 => (player.activeDialog = player.finalText) // game over
    }
  }

  def spawnBoss(): Unit = {
    sprites = sprites :+ new BossEnemy(10.5, 8.5, this)
  }

  def initSpawners(): Unit = { /// tää on ihan tosi ovela :D
    val minRequired = 4
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

    if (picked.length < minRequired) { // katotaa et aina vähintään 4 spawnerii
      val missing = minRequired - picked.length
      val extra = randomTiles.filterNot(picked.contains).take(missing)
      picked ++= extra
    }

    for ((x, y) <- picked) {
      spawnSpawner(x + 0.5, y + 0.5) // keskelle sitä tilee
      map.updateMap(x, y, 13, 0) // laitetaa siihe se pähee tile alle
    }
  }
}
