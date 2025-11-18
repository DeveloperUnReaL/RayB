package core.entities
import core.*
import core.RayCaster.{PI, castRay}
import scala.util.Random

class Enemy(
          var x: Double,
          var y: Double,
          val player: Player,
          val map: Map,
          override val texId: Int = 4,
          var hp: Int = 30,
          val damage: Int = 30,
          var speed: Double = 1.5,
          override val hitRadius: Double = 0.2,
          val score: Int = 1,
          var dead: Boolean = false,
          val detectionRadius: Double = 8.0
) extends Sprite {
  val hitFlashDuration: Double = 0.35
  var hitFlashTimer: Double = 0.0
  var hitFlash: Boolean = false

  var detectPlayer: Boolean = false

  var roamTimer: Double = 0.0
  val roamInterval: Double = 1.0

  var roamDir = Random.nextDouble() * PI * 2

  override def update(delta: Double): Unit = {
    detectPlayer(delta, player)
    hitFlashTimer -= delta
    roamTimer -= delta
    if (hitFlashTimer <= 0) hitFlash = false

    if (!detectPlayer) {
      if (roamTimer <= 0) {
        if (Random.nextDouble() < 0.3) {
          speed = 0.7
          roamDir = Random.nextDouble() * PI * 2
        } else speed = 0
        roamTimer = roamInterval
      }
      move(delta, roamDir)
    } else speed = 1.5
  }

  override def takeDamage(amount: Int): Unit = {
    if (!hitFlash) {
      hp -= amount
      hitFlash = true
      hitFlashTimer = hitFlashDuration
      if (hp <= 0) die()
    }
  }

  def die(): Unit = {
    println("enemy died!")
    dead = true
  }

  def move(delta: Double, dir: Double): Unit = {
    val nx = x + (math.cos(dir) * speed * delta)
    val ny = y + (math.sin(dir) * speed * delta)
    if map.notSolid(nx,y,1) then x = nx //Collision
    if map.notSolid(x,ny,1) then y = ny //Collision
  }

  def detectPlayer(delta: Double, player: Player): Unit = {
    val dx = player.x - x
    val dy = player.y - y

    val dir = math.atan2(dy, dx)
    val normDir = if (dir < 0) dir + 2 * PI else if (dir >= 2 * PI) dir - (2 * PI) else dir
    val distToPlayer = math.sqrt(dx*dx + dy*dy)

    val hitRay = castRay(player, normDir - PI, map, 1, 1000).firstOpaqueHit(map)

    hitRay match {
      case Some(hit) =>
        if (hit.realDistance > distToPlayer && distToPlayer < detectionRadius) {
          detectPlayer = true
          move(delta, normDir)
        } else {
          detectPlayer = false
        }
      case None =>
        detectPlayer = true
        move(delta, normDir)
    }
  }
}
