package core

// Tile types:
// 0: Empty
// 1: Wall (solid, visible)
// 2-5: Floor/ceiling textures
// 6: Door closed (solid, visible)
// 7: Door open (not solid, visible)
// 9: SpawnPoint (not solid, not visible)

class Map:
  val grid: Array[Array[Array[Int]]] = /// lvl, x, y  -- lvl 0: floor, lvl 1: walls, lvl 2: ceiling
    Array(
      Array( // Floor
        Array(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
        Array(0, 2, 2, 2, 0, 5, 0, 5, 5, 5, 5, 5, 5, 5, 0),
        Array(0, 2, 2, 2, 0, 5, 0, 5, 5, 5, 0, 5, 5, 5, 0),
        Array(0, 2, 2, 2, 0, 5, 5, 5, 5, 5, 5, 5, 5, 5, 0),
        Array(0, 0, 2, 0, 0, 5, 5, 5, 0, 5, 5, 5, 5, 5, 0),
        Array(0, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 0, 5, 0),
        Array(0, 5, 5, 5, 0, 5, 5, 5, 5, 5, 5, 5, 5, 5, 0),
        Array(0, 5, 5, 5, 0, 5, 5, 0, 0, 0, 0, 0, 5, 5, 0),
        Array(0, 5, 5, 5, 0, 5, 5, 0, 5, 5, 5, 5, 5, 5, 0),
        Array(0, 5, 5, 5, 0, 5, 5, 0, 5, 5, 5, 5, 5, 3, 0),
        Array(0, 5, 5, 5, 0, 5, 5, 5, 5, 5, 5, 5, 3, 3, 0),
        Array(0, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 3, 3, 0),
        Array(0, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 3, 3, 3, 0),
        Array(0, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 3, 3, 3, 0),
        Array(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
      ),
      Array( // Walls
        Array(1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1),
        Array(1, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 1),
        Array(1, 0, 9, 0, 1, 0, 1, 0, 0, 0, 1, 0, 0, 0, 1),
        Array(1, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 1),
        Array(1, 1, 6, 1, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1),
        Array(1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1),
        Array(1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1),
        Array(1, 0, 0, 0, 1, 0, 0, 1, 1, 1, 1, 1, 0, 0, 1),
        Array(1, 0, 0, 0, 1, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1),
        Array(1, 0, 0, 0, 1, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1),
        Array(1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 6, 0, 0, 0, 1),
        Array(1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 6, 0, 0, 0, 1),
        Array(1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 6, 0, 0, 0, 1),
        Array(1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1),
        Array(1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1),
      ),
      Array( // Ceiling
        Array(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
        Array(0, 4, 4, 4, 0, 1, 0, 1, 1, 1, 1, 1, 1, 1, 0),
        Array(0, 4, 4, 4, 0, 1, 0, 1, 1, 1, 0, 1, 1, 1, 0),
        Array(0, 4, 4, 4, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0),
        Array(0, 1, 4, 1, 0, 1, 1, 1, 0, 1, 1, 1, 1, 1, 0),
        Array(0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 0),
        Array(0, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0),
        Array(0, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 1, 1, 0),
        Array(0, 1, 1, 1, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0),
        Array(0, 1, 1, 1, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0),
        Array(1, 1, 1, 1, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0),
        Array(1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0),
        Array(1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0),
        Array(1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0),
        Array(1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0),
      )
    )

  /// the open door 7 is not solid
  def isSolid(tileId: Int): Boolean = tileId match {
    case 0 | 9 | 7 => false
    case _ => true
  }

  def isVisible(tileId: Int): Boolean = tileId match {
    case 0 | 9 => false
    case _ => true
  }

  def notSolid(x: Double, y: Double, lvl: Int): Boolean = {
    val tileId = grid(lvl)(y.toInt)(x.toInt)
    !isSolid(tileId)
  }

  def isOpaque(id: Int): Boolean = id match {
    case 7 => false
    case _ => isVisible(id)
  }

  def updateMap(x: Double, y: Double, changeTo: Int): Unit = {
    grid(1)(y.toInt)(x.toInt) = changeTo
  }

  val spawnPos: (Double, Double) = {
    val posOption = for {
      (row, i) <- grid(1).zipWithIndex
      (value, j) <- row.zipWithIndex
      if value == 9
    } yield (i + 0.5, j + 0.5)
    posOption.headOption.getOrElse((5.5, 5.5))
  }

  val size = grid(1).length
