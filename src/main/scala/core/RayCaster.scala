package core

import scala.collection.mutable.ArrayBuffer

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
    math.sqrt((bx - ax) * (bx - ax) + (by - ay) * (by - ay))

  def castRay(player: Player, angleIn: Double, map: Map, renderLayer: Int = 1, maxStep: Int = 1024): RayColumn = {
    val ang = normalizeAngle(angleIn)
    val dx = math.cos(ang)
    val dy = math.sin(ang)

    val hits = ArrayBuffer[RayHit]()

    // Players map square
    var mapX = player.x.toInt
    var mapY = player.y.toInt

    // Distance between grid lines in X and Y direction
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

    //var hit = false
    //var side = 0 // 0 -> Vertical hit (X-side), 1 -> Horizontal hit (Y-side)
    var foundOpaque = false
    var steps = 0

    while (!foundOpaque && steps < maxStep) { // Eli otetaan jokanen osuma muistiin että saadaan noi läpinäkyvätki renderöityä
      var side = 0
      if sideDistX < sideDistY then {
        sideDistX += deltaDistX
        mapX += stepX
        side = 0
      } else {
        sideDistY += deltaDistY
        mapY += stepY
        side = 1
      }

      if (mapY >= 0 && mapY < map.size && mapX >= 0 && mapX < map.size) {
        val tileId = map.grid(renderLayer)(mapY)(mapX)

        if (map.isVisible(tileId)) {
          val realDist = if (side == 0) then (mapX - player.x + (1 - stepX) / 2.0) / dx
          else (mapY - player.y + (1 - stepY) / 2.0) / dy

          val hitX = player.x + dx * realDist
          val hitY = player.y + dy * realDist
          val texX = if (side == 0) then hitY - math.floor(hitY)
          else hitX - math.floor(hitX)

          val fixedDist = realDist * math.cos(player.dir - ang)

          hits += RayHit(hitX, hitY, realDist.abs, fixedDist.abs, texX, tileId)

          if (map.isOpaque(tileId)) {
            foundOpaque = true
          }
        }
      }
      steps += 1
    }

    RayColumn(hits.toArray)
  }
}


case class RayHit(x: Double, y: Double, realDistance: Double, fixedDistance: Double, texX: Double, texId: Int = 1)
case class RayColumn(hits: Array[RayHit]) {
  def firstHit: Option[RayHit] = hits.headOption
  def firstOpaqueHit(map: Map): Option[RayHit] = hits.find(hit => map.isOpaque(hit.texId))
  def firstSolidHit(map: Map): Option[RayHit] = hits.find(hit => map.isSolid(hit.texId))
  def hasHit: Boolean = hits.nonEmpty
}