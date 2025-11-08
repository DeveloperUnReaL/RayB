package core

import java.awt.event.*

class KeyHandler(player: Player) extends KeyAdapter{
  override def keyPressed(e: KeyEvent): Unit =
    e.getKeyCode match
      case KeyEvent.VK_W => player.moveForward = true
      case KeyEvent.VK_A => player.moveLeft = true
      case KeyEvent.VK_S => player.moveBackward = true
      case KeyEvent.VK_D => player.moveRight = true
      case KeyEvent.VK_LEFT => player.turnLeft = true
      case KeyEvent.VK_RIGHT => player.turnRight = true
      case _ => ()

  override def keyReleased(e: KeyEvent): Unit =
    e.getKeyCode match
      case KeyEvent.VK_W => player.moveForward = false
      case KeyEvent.VK_A => player.moveLeft = false
      case KeyEvent.VK_S => player.moveBackward = false
      case KeyEvent.VK_D => player.moveRight = false
      case KeyEvent.VK_LEFT => player.turnLeft = false
      case KeyEvent.VK_RIGHT => player.turnRight = false
      case _ => ()
}
