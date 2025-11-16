package core
import scala.math
import RayCaster.castRay

class Player(var x: Double, var y: Double) {
  //val rayAmount = 1000000 //For testing

  var dir = math.Pi/2 //radiaaneja
  val moveSpeed = 2.5
  val rotSpeed = 2.0

  val shootCooldownTime = 0.2

  var hudSpriteId = 0
  var isShooting = false
  var shootCooldown: Double = 0.0

  var moveForward: Boolean = false
  var moveBackward: Boolean = false
  var moveLeft: Boolean = false
  var moveRight: Boolean = false
  var interact: Boolean = false
  var shoot: Boolean = false // SHOOT INPUT
  ///TODO: Mouse turning?
  var turnLeft: Boolean = false
  var turnRight: Boolean = false

  var dead: Boolean = false
  var health = 100
  var healthPercent = health / 100.0
  var score = 0
  var ammo = 10

  def update(delta: Double, map: Map): Unit = {
    val dx = math.cos(dir)
    val dy = math.sin(dir)

    if health <= 0 then {
      dead = true
    }

    if interact then {
      castRay(this, dir, map, 1, 1000).firstHit match {
        case Some(ray) =>
          println("interact - " + ray.realDistance)
          if (ray.realDistance <= 1) {
            val toggleTex = if (ray.texId == 6) 7 else if (ray.texId == 7) 6 else ray.texId
            val hitX = ray.x + 0.01 * math.cos(dir)
            val hitY = ray.y + 0.01 * math.sin(dir)
            map.updateMap(hitX, hitY, toggleTex)
            interact = false
          }
        case None => ()
      }
    }

    if (shoot && !this.isShooting) { // SHOOT INPUT
      castRay(this, dir, map, 1, 1000).firstHit match {
        case Some(ray) =>
          println("shoot - " + ray.realDistance) //TODO:
          hudSpriteId = 10 // Shooting sprite
        // Start cooldown
        case None => ()
      }
      isShooting = true
      shootCooldown = shootCooldownTime
      hudSpriteId = 10
    }

    if (shootCooldown > 0) {
      shootCooldown -= delta
      if (shootCooldown < 0) {
        shootCooldown = 0
        hudSpriteId = 0
        isShooting = false
      }
    }


    // Jos joku oikeesti lukee tätä koodia ni tää delta on aina sitä varten et se liikkumisnopeus ei riipu siitä miten usein ruutu päivitetään
    ///TODO: Diagonal movement speed
    if moveForward then {
      val nx = x + dx * moveSpeed * delta
      val ny = y + dy * moveSpeed * delta
      if map.notSolid(nx,y,1) then x = nx //Collision
      if map.notSolid(x,ny,1) then y = ny //Collision
    }
    if moveBackward then {
      val nx = x - dx * moveSpeed * delta
      val ny = y - dy * moveSpeed * delta
      if map.notSolid(nx,y,1) then x = nx //Collision
      if map.notSolid(x,ny,1) then y = ny //Collision
    }
    if moveRight then {
      val nx = x - dy * moveSpeed * delta
      val ny = y + dx * moveSpeed * delta
      if map.notSolid(nx,y,1) then x = nx //Collision
      if map.notSolid(x,ny,1) then y = ny //Collision
    }
    if moveLeft then {
      val nx = x + dy * moveSpeed * delta
      val ny = y - dx * moveSpeed * delta
      if map.notSolid(nx,y,1) then x = nx //Collision
      if map.notSolid(x,ny,1) then y = ny //Collision
    }
    if turnRight then dir += rotSpeed * delta
    if turnLeft then dir -= rotSpeed * delta

    if dir < 0 then dir += 2 * math.Pi
    if dir >= 2 * math.Pi then dir -= 2 * math.Pi
  }
}
