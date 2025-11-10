package core

import javax.imageio.ImageIO
import java.awt.image.BufferedImage
import java.io.File
import scala.collection.immutable.{Map => ScalaMap}

object TextureManager {
  val tileSize = 16 // your texture size in pixels


  private def loadTexture(path: String): BufferedImage = {
    println("loaded")
    val stream = Option(getClass.getResourceAsStream(path))
      .getOrElse(throw new RuntimeException(s"Texture not found: $path"))
    ImageIO.read(stream)
  }

  val textures: ScalaMap[Int, BufferedImage] = ScalaMap(
    1 -> loadTexture("/assets/textures/tiles/brick1.png"),
    2 -> loadTexture("/assets/textures/tiles/rocks5.png"),
    3 -> loadTexture("/assets/textures/tiles/water1.png"),
    4 -> loadTexture("/assets/textures/tiles/tiles2.png")
  )

  def getTexture(id: Int, column: Int): BufferedImage =
    //println(column)
    textures(id).getSubimage(column, 0, 1, tileSize)
    //textures.getOrElse(id, textures(1)) // fallback texture
}