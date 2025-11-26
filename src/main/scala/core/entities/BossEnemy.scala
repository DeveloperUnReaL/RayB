package core.entities

import core.*
import core.RayCaster.{PI, castRay}
import scala.util.Random

class BossEnemy(
          var x: Double,
          var y: Double,
          val game: Game,
          override val texId: Int = 9,
          var hp: Int = 500,
          val damage: Int = 30,
          var speed: Double = 1.0,
          override val hitRadius: Double = 0.2,
          val score: Int = 100,
          var dead: Boolean = false,
          var detectionRadius: Double = 5.0
) extends Sprite {
  val player = game.player
  val map = game.map
  val hitFlashDuration: Double = 0.35
  var hitFlashTimer: Double = 0.0
  var hitFlash: Boolean = false

  def hpPercent: Double = hp / 500.0

  var detectPlayer: Boolean = false

  var actionTimer: Double = 0.0
  val actionInterval: Double = 4.0

  override def update(delta: Double): Unit = {
    if dead then return
    detectPlayer(delta, player)
    hitFlashTimer -= delta
    actionTimer -= delta
    if (hitFlashTimer <= 0) hitFlash = false

    if (detectionRadius > 10) { // jos niinku ns hereillä/agrottu
      if dead then return
      if (actionTimer <= 0) {
        speed = 1.0
        val chance = Random.nextDouble()
        if (!detectPlayer && chance < 0.8) { // cheese esto :D
          teleport(player, 3)
        } else if (chance < 0.15) { // lunge 0.15
          speed = 2.3
          actionTimer = actionInterval - 2
        } else if (chance < 0.35) { // spawnaus 0.2 x2 :D
          spawnEnemies()
          spawnEnemies()
          actionTimer = actionInterval
        } else if (chance < 0.65) { // orb 0.3
          shoot()
          speed = 0.5
          actionTimer = actionInterval - 2
        } else if (chance < 0.75) { // jekku :DD 0.1
          teleport(player, 3)
          actionTimer = actionInterval - 2
        } else if (chance < 0.85) { // peruutus :DDD 0.1
          speed = -0.8
          actionTimer = actionInterval - 2.5
        } else { // paikoillaa 0.15
          speed = 0
          actionTimer = actionInterval - 3
        }
      }
    }
  }

  override def takeDamage(amount: Int): Unit = {
    detectionRadius = 20.0
    if (!hitFlash) {
      hp -= amount
      hitFlash = true
      hitFlashTimer = hitFlashDuration
      if (hp <= 0) die()
    }
  }

  def die(): Unit = {
    dead = true
    speed = 0.0
  }

  def move(delta: Double, dir: Double): Unit = {
    if dead then return
    val nx = x + (math.cos(dir) * speed * delta)
    val ny = y + (math.sin(dir) * speed * delta)
    if map.notSolid(nx,y,1) then x = nx //Collision
    if map.notSolid(x,ny,1) then y = ny //Collision
  }

  def detectPlayer(delta: Double, player: Player): Unit = { //tekee käytännös saman ku toi LOS funktio mut cant be arsed vaihtaa enää
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
          detectionRadius = 20.0
          move(delta, normDir)
        } else {
          detectPlayer = false
        }
      case None =>
        detectPlayer = true
        move(delta, normDir)
    }
  }

  def teleport(player: Player, radius: Int): Unit = {
    val px = player.x
    val py = player.y

    val candidates =
      scala.util.Random.shuffle(
        for
          dx <- -radius to radius
          dy <- -radius to radius
          if dx != 0 || dy != 0
        yield (px + dx, py + dy)
      )

    // Etitää et mihi vois teleporttaa
    for ((tx, ty) <- candidates) {
      val ix = math.max(0, math.min(tx.toInt, map.size - 1)) // out of bounds chekki
      val iy = math.max(0, math.min(ty.toInt, map.size - 1))
      if (map.notSolid(ix, iy, 1)) {
        if (hasLineOfSight(tx, ty, px, py)) {
          // TELEPORT
          x = tx
          y = ty
          actionTimer = actionInterval
        }
      }
    }
  }
   // Jeesijä
  private def hasLineOfSight(fromX: Double, fromY: Double, toX: Double, toY: Double): Boolean = {
    val dir = math.atan2(toY - fromY, toX - fromX)

    val valiaikapate = new Player(fromX, fromY, dir) // leikitään sitä pelaajaa
    val distToTarget = math.sqrt((toX - fromX)*(toX - fromX) + (toY - fromY)*(toY - fromY))
    val hit = RayCaster.castRay(valiaikapate, dir, map, 1, 1000).firstOpaqueHit(map)

    hit match {
      case Some(block) =>
        block.realDistance > distToTarget
      case None => /// on LOS
        true
    }
  }

  def spawnEnemies(): Unit = {
    val candidates =
      scala.util.Random.shuffle(
        for
          dx <- -1 to 1
          dy <- -1 to 1
          if dx != 0 || dy != 0
        yield (x + dx, y + dy)
      )

    // Etitää et mihi vois spawnata kaverin :D
    //println(candidates)
    for ((tx, ty) <- candidates) {
      val ix = math.max(0, math.min(tx.toInt, map.size - 1)) // out of bounds chekki
      val iy = math.max(0, math.min(ty.toInt, map.size - 1))
      if (map.notSolid(ix, iy, 1)) {
        game.spawnEnemy(ix, iy)
        return
      }
    }
  }

  def shoot(): Unit = {
    val px = player.x
    val py = player.y

    val dir = math.atan2(py - y, px - x)
    game.spawnOrb(x, y, dir)
    game.spawnOrb(x, y, dir + 0.15)
    game.spawnOrb(x, y, dir - 0.15)
  }
}