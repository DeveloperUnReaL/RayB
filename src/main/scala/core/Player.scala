package core
import scala.math
import RayCaster.castRay

class Player(var x: Double, var y: Double) {
  //val rayAmount = 1000000 //For testing

  var dir = 0.3 //radiaaneja
  val moveSpeed = 2.5
  val rotSpeed = 2.0

  val shootCooldownTime = 0.2

  var hintText: String = ""

  var storyText: String = ""
  val dialogSpeed = 1
  var dialogIndex = 0
  var talking = false
  var isLineDone = false
  var textProgress = 0.0
  var currentLine = ""
  var startText: Array[String] = Array(
    "Listen.",
    "You've been sent on a mission.",
    "There appear to be some evil forces around here",
    "And your task is to get rid of them...",
    "...",
    "Im not actually sure how to deal with them",
    "But try shooting at them I guess...",
    "Good luck soldier!",
  )
  var repeatText: Array[String] = Array(
    "What are you waiting for?",
    "Did my little baby get scawed?",
    "GO BACK AND TERMINATE THOSE GHOSTS"
  )
  var activeDialog: Array[String] = startText

  var hudSpriteId = 0
  var isShooting = false
  var shootCooldown: Double = 0.0

  var moveForward: Boolean = false
  var moveBackward: Boolean = false
  var moveLeft: Boolean = false
  var moveRight: Boolean = false
  var interact: Boolean = false
  var interactable: Boolean = false
  var interactCooldown: Double = 0.0
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


    /// CHECK FOR INTERACTION
    val firstHitOpt = castRay(this, dir, map, 1, 1000).firstHit
    val interactionRay: Option[RayHit] = firstHitOpt.filter(_.realDistance <= 1)
    var interactable = false

    interactionRay match {
      case Some(hit) if hit.texId == 6 || hit.texId == 7 =>
        interactable = true
        hintText = "open/close"

      case Some(hit) if hit.texId == 8 =>
        interactable = true
        hintText = "chat"

      case _ =>
        interactable = false
        hintText = ""
    }
    val lookingAtNPC = interactionRay.exists(_.texId == 8)

    if (interact && interactable && interactCooldown <= 0) then {
      interactionRay match {
        case Some(hit) =>
          hit.texId match {

            // DOOR
            case 6 | 7 =>
              val newTex = if (hit.texId == 6) 7 else 6
              val updateX = hit.x + 0.01 * math.cos(dir)
              val updateY = hit.y + 0.01 * math.sin(dir)
              map.updateMap(updateX, updateY, newTex)

            // DIALOG
            case 8 =>
              if (!talking) {
                talking = true
                dialogIndex = 0
                currentLine = activeDialog(dialogIndex)
                storyText = ""
                textProgress = 0.0
                isLineDone = false
              } else if (talking && isLineDone) {
                  dialogIndex += 1 // seuraava rivi
                  if (dialogIndex >= activeDialog.length) { // dialog loppu
                    talking = false
                    storyText = ""
                    textProgress = 0.0
                    isLineDone = false
                    activeDialog = repeatText
                  } else { // alota seuraava rivi
                    currentLine = activeDialog(dialogIndex)
                    storyText = ""
                    textProgress = 0.0
                    isLineDone = false
                  }
                } else {
                storyText = currentLine
                isLineDone = true
              }

            case _ => // nothing
          }
          interactCooldown = 0.5
        case None => ()
      }
      interact = false
    }

    if (shoot && !this.isShooting && !interact) { // SHOOT INPUT
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
    if (interactCooldown > 0) {
      interactCooldown -= delta
      hintText = ""
      if (interactCooldown < 0) {
        interactCooldown = 0
      }
    }

    def updateDialog(delta: Double, lookingAtNPC: Boolean): Unit = {
      if (!talking) return
      if !lookingAtNPC then {
        talking = false
        storyText = ""
        dialogIndex = 0
        isLineDone = false
        textProgress = 0.0
        return
      }

      if (!isLineDone) {
        textProgress += delta / dialogSpeed
        val charCount = math.min(currentLine.length, (textProgress * currentLine.length).toInt)
        storyText = currentLine.substring(0, charCount)
        if (charCount >= currentLine.length) {
          isLineDone = true
        }
      }

    }

    updateDialog(delta, lookingAtNPC)

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
