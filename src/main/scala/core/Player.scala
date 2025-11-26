package core
import scala.math
import RayCaster.castRay
import core.entities.*

class Player(var x: Double, var y: Double, var dir: Double = 0.3) {

  //var dir = 0.3 //radiaaneja
  val moveSpeed = 2.5
  val rotSpeed = 2.0
  var score = 0

  val shootCooldownTime = 0.3

  var hintText: String = ""

  var storyText: String = ""
  val dialogSpeed = 1
  var dialogIndex = 0
  var talking = false
  var isLineDone = false
  var textProgress = 0.0
  var currentLine = ""
  var startText: Array[String] = Array(
    "Listen here private.",
    "You've been sent on a mission.",
    "There appear to be some evil forces around here",
    "And your task is to get rid of them.",
    "...",
    "I don't know what will work on those SPECTRAL SCUMS...",
    "But ummm try that thing you're holding maybe?",
    "Good luck private!",
  )
  var repeatText: Array[String] = Array(
    "What are you waiting for?",
    "Did my little baby get scawed?",
    "GO BACK AND TERMINATE THOSE GHOSTLY BEINGS"
  )
  def finalText: Array[String] = Array(
    "Excellent work, the evil has been defeated.",
    "You'll be rewarded with 200C points in the Aalto O1 course.",
    "Good job, private!",
    s"Oh and btw, you finished with ${score} score.",
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
  var turnLeft: Boolean = false
  var turnRight: Boolean = false

  var dead: Boolean = false
  var health = 100
  var healthPercent = 1.0
  var ammo = 10
  val damage = 14

  val hitRadius = 1.2

  val hurtCooldownDuration = 1
  var hurtCooldownTimer: Double = 0.0
  var hurtCooldown: Boolean = false

  val hitFlashDuration: Double = 0.2
  var hitFlashTimer: Double = 0.0
  var hitFlash: Boolean = false

  var healFlashDuration: Double = 0.2
  var healFlashTimer: Double = 0.0
  var healFlash: Boolean = false

  var dx: Double = math.cos(dir)
  var dy: Double = math.sin(dir)
  var planeX: Double = 0.0
  var planeY: Double =  0.0

  def checkHurtBox(game: Game): Unit = { // legit katotaan vaa et onks mikää vihu liian lähellä /// miks oon tehy tän näin tyhmästi???
    for (sprite <- game.sprites) {
      sprite match {
        case s: Enemy =>
          val dx = s.x - x
          val dy = s.y - y
          val distance = math.sqrt(dx*dx + dy*dy)

          if (distance <= s.hitRadius) {takeDamage(s.damage)}
        case s: BossEnemy =>
          val dx = s.x - x
          val dy = s.y - y
          val distance = math.sqrt(dx*dx + dy*dy)

          if (distance <= s.hitRadius) {takeDamage(s.damage)}
        case s: Orb =>
          val dx = s.x - x
          val dy = s.y - y
          val distance = math.sqrt(dx*dx + dy*dy)

          if (distance <= s.hitRadius) {takeDamage(s.damage)}
        case _ => ()
      }
    }
  }

  def takeDamage(amount: Int): Unit = { //yeaouch
    if (!hurtCooldown) {
      health -= amount
      hurtCooldown = true
      hurtCooldownTimer = hurtCooldownDuration
      hitFlash = true
      hitFlashTimer = hitFlashDuration
    }
  }

  def heal(amount: Int): Unit = {
    health = math.min(health + amount, 100) /// miks mulla ei oo maxhp variablee??
    healFlash = true
    healFlashTimer = healFlashDuration
  }

  def update(delta: Double, map: Map, game: Game): Unit = {
    dx = math.cos(dir)
    dy = math.sin(dir)
    planeX = -dy * math.tan(game.fov/2)
    planeY = dx * math.tan(game.fov/2)

    checkHurtBox(game)

    healthPercent = health / 100.0 // ui piirtämist varten

    if health <= 0 then {
      dead = true
      return
    }

    /// CHECK FOR INTERACTION, eli katotaan joka frame et onks edessä joku minkä kaa vois mahollisesti interaktoida
    /// käytännössä sitä varten et voidaan näyttää se pieni teksti
    val firstHitOpt = castRay(this, dir, map, 1, 1000).firstHit
    val interactionRay: Option[RayHit] = firstHitOpt.filter(_.realDistance <= 1)
    var interactable = false

    interactionRay match {
      case Some(hit) if hit.texId == 6 || hit.texId == 7 =>
        if !(activeDialog sameElements startText) then // ei päästetä pelaajaa ulos ilman loree :DD
          interactable = true
          hintText = "open/close(E)"

      case Some(hit) if hit.texId == 8 =>
        interactable = true
        hintText = "chat(E)"

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
            case 8 => // tää oli ihan älyttömän kova järjestelmä kanssa, tätä voi laajentaa vaikka ja miten.
              if (!talking) {
                talking = true
                dialogIndex = 0
                currentLine = activeDialog(dialogIndex)
                storyText = ""
                textProgress = 0.0
                isLineDone = false
              } else if (talking && isLineDone) {
                  dialogIndex += 1 // seuraava rivi
                  if (dialogIndex >= activeDialog.length) { // dialog loppu /// holy nest :DDD
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
          interactCooldown = 0.4
        case None => ()
      }
      interact = false
    }

    if (shoot && !this.isShooting && !interact) { // SHOOT INPUT
      val dx = math.cos(dir)
      val dy = -math.sin(dir)
      castRay(this, dir, map, 1, 1000).firstSolidHit(map) match {
        case Some(ray) =>
          hudSpriteId = 10 // Shooting sprite
          enemyHit(ray)
        case None => // tää on semmoselle tilanteelle et se ray ei osu mihinkään seinään. Vähä tyhmää joo mut :DD
          val enemyHits = game.sprites.flatMap { enemy => rayIntersectsEnemy(dx,dy,enemy).map(dist => (enemy, dist))}
          enemyHits.sortBy(_._2).headOption match {
            case Some((enemy, dist)) =>
              enemy.takeDamage(damage)
            case None => ()
          }
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
    if (hurtCooldownTimer > 0) {
      hurtCooldownTimer -= delta
      if (hurtCooldownTimer < 0) {
        hurtCooldownTimer = 0
        hurtCooldown = false
      }
    }
    if (hitFlashTimer > 0) {
      hitFlashTimer -= delta
      if (hitFlashTimer < 0) {
        hitFlashTimer = 0
        hitFlash = false
      }
    }
    if (healFlashTimer > 0) {
      healFlashTimer -= delta
      if (healFlashTimer < 0) {
        healFlashTimer = 0
        healFlash = false
      }
    }
    if (interactCooldown > 0) {
      interactCooldown -= delta
      hintText = ""
      if (interactCooldown < 0) {
        interactCooldown = 0
      }
    }

    def enemyHit(ray: RayHit): Unit = { // ja tosiaan tää on siihen et jos siel on seinä siel takana
      val dx = math.cos(dir)
      val dy = math.sin(dir)

      val wallDist = ray.realDistance

      val enemyHits = game.sprites.flatMap { enemy =>
        rayIntersectsEnemy(dx,dy,enemy).map(dist => (enemy, dist))
      }
      val nearest = enemyHits.sortBy(_._2).headOption

      nearest match {
        case Some((enemy, dist)) if dist < wallDist =>
          enemy.takeDamage(damage)
        case _ => ()
      }
    }

    def rayIntersectsEnemy(dx: Double, dy: Double, enemy: Sprite): Option[Double] = { // jeesaileva
      val diffX = enemy.x - this.x
      val diffY = enemy.y - this.y

      val dist = diffX * dx + diffY * dy
      if (dist < 0) return None // Jos se on siis takana

      val cx = this.x + dx * dist
      val cy = this.y + dy * dist

      val distSq = (cx - enemy.x)*(cx - enemy.x) + (cy - enemy.y)*(cy - enemy.y)

      if (distSq <= enemy.hitRadius * enemy.hitRadius) Some(dist)
      else None
    }

    def updateDialog(delta: Double, lookingAtNPC: Boolean): Unit = { // eli se mikä saa sen tekstin sillee tulee vähitellen
      if (!talking) return
      if !lookingAtNPC then { // pitää keskittyy puhumiseen hei, ei voi kattoo pois :D #käytöstavat
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
    /// teorias vois normalisoida ton nopeuden, ei jaksa.
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
