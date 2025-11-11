package core
import scala.math
import RayCaster.castRay
import java.awt.event._

class Player(var x: Double, var y: Double) {
  //val rayAmount = 1000000 //For testing

  var dir = math.Pi/2 //radiaaneja
  val moveSpeed = 3.5
  val rotSpeed = 4.0

  var moveForward: Boolean = false
  var moveBackward: Boolean = false
  var moveLeft: Boolean = false
  var moveRight: Boolean = false
  var interact: Boolean = false
  ///TODO: Mouse turning?
  var turnLeft: Boolean = false
  var turnRight: Boolean = false

  def update(delta: Double, map: Map): Unit = {
    val dx = math.cos(dir)
    val dy = math.sin(dir)

    if interact then {
      castRay(this, dir, map, 1, 1000).firstHit match {
        case Some(ray) =>
          println("interact - " + ray.realDistance)
          if (ray.realDistance <= 1) {
            if (ray.texId == 6) {
              map.updateMap(ray.x, ray.y, 7)
            } else if (ray.texId == 7) {
              map.updateMap(ray.x, ray.y, 6)
            }
            interact = false
          }
        case None => ???
      }
    }


    // Jos joku lukee tätä koodia ni tää delta on aina sitä varten et se liikkumisnopeus ei riipu siitä miten usein ruutu päivitetään
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
