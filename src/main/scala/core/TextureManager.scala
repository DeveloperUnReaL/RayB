package core

import javax.imageio.ImageIO
import java.awt.*
import java.awt.image.*
import scala.collection.immutable.Map as ScalaMap

object TextureManager {

  val tileSize = 32
  val spriteSize = 64

  private def loadTexture(path: String): BufferedImage = {
    println("loaded")
    val stream = Option(getClass.getResourceAsStream(path))
      .getOrElse(throw new RuntimeException(s"Texture not found: $path"))
    ImageIO.read(stream)
  }

  val textures: ScalaMap[Int, BufferedImage] = ScalaMap(
    1 -> loadTexture("/assets/textures/tiles/tile811.png"), // normal wall
    2 -> loadTexture("/assets/textures/tiles/tile1073.png"), // non spawnable floor tile
    3 -> loadTexture("/assets/textures/tiles/water1.png"),
    4 -> loadTexture("/assets/textures/tiles/tile1104.png"), // roof tile
    5 -> loadTexture("/assets/textures/tiles/tile1029.png"), // spawnable grass
    6 -> loadTexture("/assets/textures/tiles/doorclosed.png"),
    7 -> loadTexture("/assets/textures/tiles/dooropen.png"),
    8 -> loadTexture("/assets/textures/tiles/npc.png"),
    // 9 is reserved for player spawn
    10 -> loadTexture("/assets/textures/tiles/tile334.png"), // fence
    11 -> loadTexture("/assets/textures/tiles/tile1107.png"), // spawnable floor tile
    12 -> loadTexture("/assets/textures/tiles/tile1029.png"),
    13 -> loadTexture("/assets/textures/tiles/ground_skulls1.png"),
    14 -> loadTexture("/assets/textures/tiles/ground_blood1.png"),
    100 -> loadTexture("/assets/textures/skybox/skybox.png"),
  )

  val spriteTextures: ScalaMap[Int, BufferedImage] = ScalaMap(
    0 -> loadTexture("/assets/textures/sprites/gun.png"),
    1 -> loadTexture("/assets/textures/sprites/pillar.png"),
    2 -> loadTexture("/assets/textures/sprites/plant.png"),
    3 -> loadTexture("/assets/textures/sprites/table.png"),
    4 -> loadTexture("/assets/textures/sprites/ghost.png"),
    5 -> loadTexture("/assets/textures/sprites/barrel.png"),
    8 -> loadTexture("/assets/textures/sprites/spawner.png"),
    9 -> loadTexture("/assets/textures/sprites/boss.png"),
    10 -> loadTexture("/assets/textures/sprites/gun_shoot.png"),
    15 -> loadTexture("/assets/textures/sprites/bossdead.png"),
    20 -> loadTexture("/assets/textures/sprites/boneblood.png"),
    21 -> loadTexture("/assets/textures/sprites/bonepile.png"),
    22 -> loadTexture("/assets/textures/sprites/deadplant.png"),
    23 -> loadTexture("/assets/textures/sprites/hanging.png"),
    24 -> loadTexture("/assets/textures/sprites/lamp.png"),
    25 -> loadTexture("/assets/textures/sprites/blood.png"),
    26 -> loadTexture("/assets/textures/sprites/leaf.png"),
  )

  def getTexture(id: Int): BufferedImage =
    textures.getOrElse(id, textures(1))

  def getTexture(id: Int, column: Int): BufferedImage = {
    val tex = textures(id)
    val col = math.max(0, math.min(column, tex.getWidth - 1))
    tex.getSubimage(col, 0, 1, tileSize)
  }

  def getTexturePixel(id: Int, x: Int, y: Int): Color = {
    val tex = textures(id)
    val xx = math.max(0, math.min(x, tex.getWidth - 1))
    val yy = math.max(0, math.min(y, tex.getHeight - 1))
    new Color(tex.getRGB(xx, yy), true)
  }

  def getSprite(id: Int): BufferedImage = spriteTextures.getOrElse(id, spriteTextures.head._2)

  def getSpritePixel(id: Int, x: Int, y: Int): Color = {
    val xx = math.max(0, math.min(x, getSprite(id).getWidth - 1))
    val yy = math.max(0, math.min(y, getSprite(id).getHeight - 1))
    new Color(getSprite(id).getRGB(xx, yy), true)
  }
}