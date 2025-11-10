package core

import javax.imageio.ImageIO
import java.awt.image.BufferedImage
import java.io.File
import scala.collection.immutable.{Map => ScalaMap}

object TextureManager {
  val tileSize = 16 // your texture size in pixels

  val textures: ScalaMap[Int, BufferedImage] = ScalaMap(
    1 -> ImageIO.read(new File("assets/brick.png")),
    2 -> ImageIO.read(new File("assets/stone.png")),
    3 -> ImageIO.read(new File("assets/metal.png")),
    4 -> ImageIO.read(new File("assets/wood.png"))
  )

  def getTexture(id: Int): BufferedImage =
    textures.getOrElse(id, textures(1)) // fallback texture
}