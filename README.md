# RayBlaster 0.1
Aalto O1 course project (+a personal project)
101054230

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
- The enemy has a detection distance, but it also relies on line of sight. If the player goes behind a wall, the enemy forgets about it and starts roaming around. When the enemy dies, it has a 10% chance to drop a healing blob
- The boss chooses between a few actions about every 2-4 seconds or so:
  1. Spawning 2 normal enemies,
  2. Rushing the player with 2x speed,
  3. Teleporting near the player,
  4. Shooting orbs at the player,
  5. Stopping for a moment,
  6. Backing away,
  7. doing nothing.
  IF the boss doesn't have LOS when it chooses an action, It automatically teleports near the player to an empty square with a LOS to the player.

### What you see in the 2D-view
- The view first renderes the second layer of the map (Walls).
- Then, the players Rays get drawn as green rays.
- Then, the different spriteObjects get drawn where:
    1. Spawner is colored RED
    2. Enemy is colored Orange
    3. Boss is colored RED
    4. HealthBlob is colored GREEN
    5. normal SpriteObject is colored GRAY
- Then, the player is drawn with a red direction line.


## TODO:
- sounds
- main menu
- window rescaling
- settings
- some kind of shaders?
- better decoration
- more mob types
- balancing
- animations

## struggles and stuff
Storytime.

Ihan alussa ku nää kaikki ideat oli auki, mä mietin vaan että ois kiva tehä jotain vähän visuaalisempaa ku iha vaan perus tekstiseikkailu. Mulla oli ollu suunnitelmana tehä vapaa-ajalla tommonen raycaster mut rustilla, mut mietin että tää ois ihan täydellinen hetki kokeilla suorittaa joku vähän isompi projekti. Muutenki tää oli pakko saada valmiiks ku oli semi tiukka deadline, ni päätin sit vähän haastaa itteeni. 

Eka juttu mitä mä tein sit ku oli saanu projektin kasaan oli sen rakenteen hahmottaminen. Mä oon tehy aikasemmin useitaki pelejä (en toki ihan näin perusteellisesti), niin mulla oli jo hyvä idea siitä miten tää kannattais jakaa komponentteihin. Se mikä oli aluks kyllä vähän hankalaa oli se että miten mä saan ees asioita piirrettyä näytölle, mut tääki ratkes tekoälyn konsultoinnin kautta. Javassa/Scalassa oli näköjään oma kirjasto, millä pysty piirtää tämmösii simppeleitä kuvia ja muotoja ruudulle. No tällä mä sit aloin aluks tekemään tota 2D- näkymää, joka sit jeesais tulevaisuudessa devaamisen kanssa jne. Kartta mulla tuli aika luonnostaan, mä olin meinaa aikasemminki tehy tämmösen karttapohjasen projektin.  Nyt mä vaan sit visualisoin sen tällee vähän fiinimmin. Tän kartan jälkeen sit alko tää oikee rayCasterin koodaaminen. Tää oliki vähän vaikeempi prosessi, sillä jonkun ray:n osuman laskeminen ees 2D-maailmassa on oikeesti aika vaikeeta.

Aluks yritin käyttää semmosta menetelmää joka ampuuki kaks rayta: Eka ray kattoo horisontaalisia seinän palasia ja osuu vaan niihin, ja toka kattoo sit vertikaalisia seinän palasia. Tän jälkeen vertaillaan että kumpi on lyhyempi ja otetaan sit se käyttöön. Mut tässä oli useita ongelmia. Tää oli vähän monimutkasempi kun mitä ois tarvinn
