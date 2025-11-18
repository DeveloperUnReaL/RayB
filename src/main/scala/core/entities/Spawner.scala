package core.entities
import core.*
import core.RayCaster.{PI, castRay}

import scala.util.Random

class Spawner(
  var x: Double,
  var y: Double,
  val game: Game,
  override val texId: Int = 8,
  override val hitRadius: Double = 0.4,
  var hp: Int = 100,
  val damage: Int = 0,
  val speed: Double = 0,
  var dead: Boolean = false,
) extends Sprite {
  val player: Player = game.player
  val map: Map = game.map
  val hitFlashDuration: Double = 0.5
  var hitFlashTimer: Double = 0.0
  var hitFlash: Boolean = false

  var spawnable: Boolean = false // activates if player is seen
  val spawnDelay: Double = 15.0
  var spawnTimer: Double = 0.0
  val spawnChance: Double = 1 //prossaa %
  var spawned: Boolean = false

  val score: Int = 10


  override def update(delta: Double): Unit = {
    hitFlashTimer -= delta
    if (!spawnable) detectPlayer(delta, player)
    spawnTimer -= delta

    if (hitFlashTimer <= 0) hitFlash = false

    if (spawnTimer <= 0 && spawnable) { // eli heti ku spawntimer loppuu se yrittää joka frame spawnaa enemyt jollai 2% chancella
      if Random.nextDouble() > spawnChance * delta then return
      println(spawnChance * delta)
      spawnEnemies(game)
      spawnTimer = spawnDelay

    }
  }

  override def takeDamage(amount: Int): Unit = {
    if (!hitFlash) {
      hp -= amount
      hitFlash = true
      hitFlashTimer = hitFlashDuration
      if (hp <= 0) die()
    }
  }

  def spawnEnemies(game: Game): Unit = {
    val amount = Random.nextInt(3) + 1

    for (_ <- 0 until amount) {
      val eX = this.x + ((Random.nextDouble() * 3) - 0.9)
      val eY = this.y + ((Random.nextDouble() * 3) - 0.9)

      game.spawnEnemy(eX, eY)
    }
  }

  def die(): Unit = {
    println("spawner died!")
    dead = true
  }

  def detectPlayer(delta: Double, player: Player): Unit = {
    val dx = player.x - x
    val dy = player.y - y

    val dir = math.atan2(dy, dx)

    val normDir = { // turha :D
      if (dir < 0) dir + 2 * PI
      else if (dir >= 2 * PI) dir - (2 * PI)
      else dir
    }

    val distToPlayer = math.sqrt(dx*dx + dy*dy)

    val hitRay = castRay(player, normDir - PI, map, 1, 1000).firstOpaqueHit(map)

    hitRay match{
      case Some(hit) => {
        if (hit.realDistance > distToPlayer) {
          spawnable = true
          println("ACTIVATED")
        }
      }
      case None => ()
    }
  }
}