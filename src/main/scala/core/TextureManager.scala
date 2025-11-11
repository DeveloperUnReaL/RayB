package core

import javax.imageio.ImageIO
import java.awt.*
import java.awt.image.*
import scala.collection.immutable.Map as ScalaMap

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
    4 -> loadTexture("/assets/textures/tiles/tiles2.png"),
    5 -> loadTexture("/assets/textures/tiles/grass1.png"),
    6 -> loadTexture("/assets/textures/tiles/doorclosed.png"),
    7 -> loadTexture("/assets/textures/tiles/dooropen.png"),
  )

  val spriteTextures: ScalaMap[Int, BufferedImage] = ScalaMap(
    1 -> loadTexture("/assets/textures/sprites/pillar.png"),
  )

  val spriteHeight = 16 //TODO: FIX LATETR
  val spriteWidth = 16

  def getTexture(id: Int) = textures.getOrElse(id, textures(1)) //TEXTURE

  def getTexture(id: Int, column: Int): BufferedImage = //COLUMN
    //println(column)
    textures(id).getSubimage(column, 0, 1, tileSize)
    //textures.getOrElse(id, textures(1)) // fallback texture

  def getTexturePixel(id: Int, x: Int, y: Int): Color =
    new Color(textures(id).getRGB(x,y))

  def getSprite(id: Int) = spriteTextures.getOrElse(id, textures(1))

  def getSprite(id: Int, column: Int): BufferedImage = spriteTextures(id).getSubimage(column, 0, 1, spriteWidth)
}