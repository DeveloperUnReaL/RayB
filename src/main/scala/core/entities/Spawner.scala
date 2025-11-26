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
  val spawnDelay: Double = 8.0
  var spawnTimer: Double = 0.0
  val spawnChance: Double = 1 //prossaa %
  var spawned: Boolean = false

  val score: Int = 10


  override def update(delta: Double): Unit = {
    hitFlashTimer -= delta
    if (!spawnable) detectPlayer(delta, player)
    spawnTimer -= delta

    if (hitFlashTimer <= 0) hitFlash = false

    if (spawnTimer <= 0 && spawnable) { // eli heti ku spawntimer loppuu se yrittää joka frame spawnaa enemyt jollai 1% chancella
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
    if dead then return
    val amount = Random.nextInt(3) + 2
    var spawned = 0

    while (spawned < amount) {
      val eX = this.x + ((Random.nextDouble() * 3) - 0.9)
      val eY = this.y + ((Random.nextDouble() * 3) - 0.9)

      val ix = math.max(0, math.min(eX.toInt, map.size - 1))
      val iy = math.max(0, math.min(eY.toInt, map.size - 1))

      if map.notSolid(ix, iy, 1) then
        game.spawnEnemy(eX, eY)
        spawned += 1
    }
  }

  def die(): Unit = {
    println("spawner died!")
    dead = true
  }

  def detectPlayer(delta: Double, player: Player): Unit = { // tässäki on tää sama funktio, ei oo fiksusti tehty
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