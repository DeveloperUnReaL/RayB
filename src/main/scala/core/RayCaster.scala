package core

object RayCaster {
  val PI = math.Pi
  val PI2 = math.Pi / 2
  val PI3 = (math.Pi * 3) / 2
  val PI4 = math.Pi * 2

  private def normalizeAngle(angle: Double): Double = {
    var nAngle = angle % PI4
    if nAngle < 0 then nAngle += PI4
    nAngle
  }

  private def dist(ax: Double, ay: Double, bx: Double, by: Double) =
    (math.sqrt((bx-ax)*(bx-ax) + (by-ay)*(by-ay)))


  def castRay(player: Player, angleIn: Double, map: Map, renderLayer: Int = 1, maxStep: Int = 1024): RayHit = {
    val ang = normalizeAngle(angleIn)
    val dx = math.cos(ang)
    val dy = math.sin(ang)

    // Players map square
    var mapX = player.x.toInt
    var mapY = player.y.toInt

    val deltaDistX = if dx == 0.0 then Double.PositiveInfinity else math.abs(1.0 / dx)
    val deltaDistY = if dy == 0.0 then Double.PositiveInfinity else math.abs(1.0 / dy)

    val stepX = if dx < 0 then -1 else 1
    val stepY = if dy < 0 then -1 else 1

    var sideDistX = {
      if dx > 0 then (mapX + 1.0 - player.x) * deltaDistX
      else (player.x - mapX) * deltaDistX
    }
    var sideDistY = {
      if dy > 0 then (mapY + 1.0 - player.y) * deltaDistY
      else (player.y - mapY) * deltaDistY
    }

    var hit = false
    var side = 0 // 0 -> Vertical hit (X-side), 1 -> Horizontal hit (Y-side)
    var steps = 0

    while (!hit && steps < maxStep) {
      if sideDistX < sideDistY then {
        sideDistX += deltaDistX
        mapX += stepX
        side = 0
      } else {
        sideDistY += deltaDistY
        mapY += stepY
        side = 1
      }

      if ((mapY >= 0) && (mapY < map.size) && (mapX >= 0) && (mapX < map.size) && (map.grid(mapY)(mapX) == renderLayer)) hit = true
      steps += 1
    }

    if (!hit) {
      val far = 1000.0
      val hx = player.x + dx * far
      val hy = player.y + dy * far
      val dist = this.dist(hx, hy, player.x, player.y)
      RayHit(hx, hy, hit = false, dist, dist) //TODO: Fixed distance
    } else {
      val dist = if (side == 0) {
        (mapX - player.x + (1 - stepX) / 2.0) / dx
      } else {
        ((mapY - player.y + (1 - stepY) / 2.0) / dy).abs
      }
      val hitX = player.x + dx * dist
      val hitY = player.y + dy * dist
      RayHit(hitX, hitY, hit = true, dist, dist) //TODO: Fixed distance
    }
  }
}

case class RayHit(x: Double, y: Double, hit: Boolean, realDistance: Double, fixedDistance: Double)