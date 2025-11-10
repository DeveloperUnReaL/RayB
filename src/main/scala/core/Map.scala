package core

//0: Empty
//1: Wall
//2:
//3:
//4:
//5: Door
//9: SpawnPoint

class Map:
  val grid: Array[Array[Int]] =
    Array(
      Array(1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1),
      Array(1, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 1),
      Array(1, 0, 9, 0, 1, 0, 1, 0, 0, 0, 1, 0, 0, 0, 1),
      Array(1, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 1),
      Array(1, 1, 0, 1, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1),
      Array(1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1),
      Array(1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1),
      Array(1, 0, 0, 0, 1, 0, 0, 1, 1, 1, 1, 1, 0, 0, 1),
      Array(1, 0, 0, 0, 1, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1),
      Array(1, 0, 0, 0, 1, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1),
      Array(1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1),
      Array(1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1),
      Array(1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1),
      Array(1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1),
      Array(1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1),
    )

  def notSolid(x: Double, y: Double): Boolean =
    grid(y.toInt)(x.toInt) == 0 || grid(y.toInt)(x.toInt) == 9

  val spawnPos: (Double, Double) = {
    val posOption = for {
      (row, i) <- grid.zipWithIndex
      (value, j) <- row.zipWithIndex
      if value == 9
    } yield(i + 0.5, j + 0.5)
    posOption.headOption.getOrElse((5.5, 5.5))
  }

  val size = grid.length
