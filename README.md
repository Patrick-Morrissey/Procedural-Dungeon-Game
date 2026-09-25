# Procedural Dungeon Game

A Java-based procedural dungeon game developed as part of the Algorithms for Game Development module. The project builds upon a base game and development guidance provided by the lecturer, which I expanded across three assignments by implementing a new procedural world generation algorithm, additional enemies and mechanics, new items, tactical encounters and gameplay systems. The game was built using IntelliJ IDEA Community.

## Features
### Procedural Dungeon Generation

- New procedural world generation approach combining Random Room Placement and Drunkard's Walk
- Cellular automata-style smoothing to refine generated rooms
- Flood fill used to identify connected dungeon regions
- Custom L-shaped corridor generation between dungeon regions
- Procedurally generated multi-level dungeons
- Stairs connecting dungeon levels
- Configurable room size and generation parameters
- Custom Region class for storing region data
- Maps used to store and access regions more efficiently
- Improved corridor generation to avoid unnecessary connections and isolated room networks

Examples of Generated Dungeons:
<p><img width="400" alt="image" src="https://github.com/user-attachments/assets/54496bb1-0b3b-4e66-a5c3-58abf3a6e1ac" />
<img width="400" alt="image" src="https://github.com/user-attachments/assets/69a5aaa5-883a-4af6-afd0-ec9e5af67100" />
<img width="400" alt="image" src="https://github.com/user-attachments/assets/4b9ef9a1-ba2c-453f-ad49-c689ef9b5503" />
</p>

### Dungeon Quest and Gameplay

- Multi-level dungeon quest involving exploration and resource management
- Locked chamber containing the victory item
- Key-based progression with dynamite as an alternative method of entry
- Inventory management system
- Weapons, armour, healing items and consumables
- Multiple win and loss conditions
- Player progression and upgrade system
- HUD displaying hunger and current dungeon level

### Enemies and AI

- New enemies with unique behaviours and gameplay mechanics
- Skeletons with ranged attacks and evasive movement
- Ghouls that actively hunt the player
- Phantoms that can only be seen while using a lamp
- Rift worms with high health and damage that can dig through walls
- Enemy behaviour based on player visibility and movement
- Different attack and movement behaviours for different enemy types

### Tactical Gameplay

- Fire hazards that damage the player and enemies
- Water jugs used to extinguish fire
- Fire resistance potions
- Lamp mechanic used to reveal hidden enemies
- Throwable items and ranged combat
- Environmental hazards that can be used against enemies
- Multiple approaches to encounters and dungeon exploration

  Images showing water jugs being used to extinguish fire. The image on the left displays a room with fire (orange asterisk) before throwing the water jug. The image on the right displays a room with fire before throwing the water jug. Water also restores some health.

<img width="400" alt="image" src="https://github.com/user-attachments/assets/104d480d-fd2c-4f80-988b-a9f4d98e8528" />
<img width="400" alt="image" src="https://github.com/user-attachments/assets/2bf506ba-6dea-4195-9ef4-e44d7372bb01" />


### Algorithms and Data Structures

- Procedural dungeon generation using multiple generation techniques
- Random Room Placement and Drunkard's Walk
- Flood fill for identifying connected regions
- Custom algorithms for connecting dungeon regions
- Algorithms for selecting valid corridor start and end points
- Region tracking using custom classes, Lists and Maps
- Improved region lookup using Maps instead of repeatedly iterating over the entire dungeon grid
- Randomised room and item placement
- Enemy movement and behaviour algorithms

## Technical Improvements

One of the main technical improvements I made was changing how dungeon regions were stored and accessed.

The original approach repeatedly iterated over the entire dungeon grid and used multiple lists to track region information. I developed a Region class to store each region's ID and the tiles it contained. I then used Maps to store the regions for each dungeon level, allowing specific regions to be accessed more efficiently without repeatedly searching the entire grid.

I also developed a custom corridor connection algorithm to reduce unnecessary connections between rooms and prevent isolated networks of rooms from being created.

## Skills Developed

This project helped me develop my skills in:

Java
- Object-oriented programming
- Algorithms and data structures
- Procedural generation
- Randomised algorithms
- Flood fill algorithms
- Working with Lists and Maps
- Designing custom classes
- Enemy AI and behaviour
- Game state and gameplay systems
- Problem-solving and debugging
- Improving code efficiency
- Extending an existing codebase
- Designing and balancing gameplay mechanics

## How to Run

Download or clone this repository.
Open the project in your preferred IDE.
Build the project.
Run the game.

## Controls

All available in-game by accessing the help window
