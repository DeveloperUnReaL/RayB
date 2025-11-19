# RayBlaster 0.1
Aalto O1 course project (+a personal project)

Table of contents:
- quick introduction
- gameplay loop
- notes & technical stuff
- stuff to implement next
- "dev diary"

---
## Quick Introduction

### KeyBinds:
- move and strafe: W, A, S, D
- turn: Left and Right Arrows
- shoot: Space
- interact: E

### Startup:
When the game (hopefully) starts, you're greeted with 2 windows. The windows are both a different kind of visual representation of the game world.
The one you're supposed to look at while playing the game is ofcourse the 3d view. If you try looking around, you may notice that you're in a small room, and there is a small hole in the wall next to you. Try interacting with the shadowy figure poking from the wall.

### Now the fun part:
After speaking with him, you'll notice that you're able to open the door. But watch out! You may be greeted with multiple enemies.
There are atleast 4 (semi)randomly placed spawners scattered around the map. These spawners activate when they see you, and instantly start spawning enemies, so be quick.
After all of these are destroyed, You'll notice that something has appeared in the middle of the map.

After defeating the last enemy, you can go back to where you started, and talk to the wall again.

GOOD JOB! You finished the game :D

## GAMEPLAY CHEAT GUIDE:
- You can just hold space and shoot infinitely
- The movement isn't normalized, so you can press 2 movement keys at once and move diagonally at a really fast speed
- If you destroy all the spawners, every enemy dies instantly
- You can abuse doors in many ways.

## Technical stuff / Enemy behavior
- The spawners activate when they can see the player (from any distance). The spawner spawns 2-4 enemies every 8 seconds or so.
- The enemy has a detection distance, but it also relies on line of sight. If the player goes behind a wall, the enemy forgets about it and starts roaming around.
- The boss chooses between 4 actions about every 2-4 seconds or so:
  1. Spawning a normal enemy,
  2. Rushing the player with 2x speed,
  3. Teleporting near the player,
  4. doing nothing.
  IF the boss doesn't have LOS when it chooses an action, It automatically teleports near the player to an empty square with a LOS to the player.

### What you see in the 2D-view
- The view first renderes the second layer of the map (Walls).
- Then, the players Rays get drawn as green rays.
- Then, the different spriteObjects get drawn where:
    1. Spawner is colored RED
    2. Enemy is colored Orange
    3. Boss is colored RED
    4. normal SpriteObject is colored PINK
- Then, the player is drawn with a red direction line.


## Dev Diary - (Finnish)
TO BE WRITTEN, I have spent almost a hundred hours during two weeks making this project, and I want to rest now.
