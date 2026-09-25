// Name: Patrick Morrissey K00218348
// Date: 09/05/2026
// Function: CA3 Encounter Design and Tactical Play

package roguelike.screens;

import java.awt.Color;
import java.awt.event.KeyEvent;
import asciiPanel.AsciiPanel;
import roguelike.*;

import java.util.ArrayList;
import java.util.List;

// The PlayScreen class will be responsible for showing the dungeon and all it’s inhabitants and loot.
// It will also respond to user input by moving
// the player and either setting us in “win” mode if we won the game, or “lose” mode if we lost.
public class PlayScreen implements Screen
{
    private World world;
    private Creature player;
    private StuffFactory stuffFactory;

    private int screenWidth;
    private int screenHeight;
    boolean victoryItemFound;
    boolean rushEventHappened;

    private FieldOfView fov;
    private List<String> messages;

    // We can have the PlayScreen know if
    // we’re working with another subscreen and delegate input and output to that screen. Once
    // the subscreen is done, it gets set to null and the PlayScreen works as normal
    private Screen subscreen;



    public PlayScreen()
    {
        screenWidth = 80;
        screenHeight = 23;
        victoryItemFound = false;
        rushEventHappened = false;

        // messages list is created in playscreen, then passed to creature factory which then passes it to player ai
        messages = new ArrayList<String>();

        createWorld();
        // Since the FieldOfView requires a world to be passed in the constructor and we don’t want the AIs to know about the world, we can
        // build the FieldOfView elsewhere and rely on constructor injection to give it to the PlayerAI. This means it will have to be passed into the
        // StuffFactory from the PlayScreen
        this.fov = new FieldOfView(world);

        // make a StuffFactory and use it to create the player’s creature
        this.stuffFactory = new StuffFactory(world);
        createCreatures(stuffFactory); // call the method to create and add creatures including the player into the world
        createItems(stuffFactory);
    }
    private void createWorld()
    {
        // this can be used to modify the size of the grid
        world = new WorldBuilder(90, 32,5).makeCaves().build();
    }

    public int getScrollX() { return Math.max(0, Math.min(player.x - screenWidth / 2, world.width() - screenWidth)); }
    public int getScrollY() { return Math.max(0, Math.min(player.y - screenHeight / 2, world.height() - screenHeight)); }

    // display and control our @ rather than scrolling on its own.
    public void displayOutput(AsciiPanel terminal)
    {
        int left = getScrollX();
        int top = getScrollY();

        displayTiles(terminal, left, top);
        //displayCreatures(terminal, left, top); // calls the displayCreatures method to display the player and creatures
        // calls the method to display all messages
        displayMessages(terminal, messages);

        // displays the player health and hunger stats
        String stats = String.format(" %3d/%3d hp \t %8s \t %4d Level \t %1d Dungeon Depth", player.hp(), player.maxHp(), hunger(), player.level(), player.z + 1);
        terminal.write(stats, 1, 23);

        // After we displayOutput the subscreen should get a chance to display. This way the
        // current game world will be a background to whatever the subscreen wants to show
        if (subscreen != null)
            subscreen.displayOutput(terminal);

    }

    // adds the world and its tiles to the terminal ascii panel
    private void displayTiles(AsciiPanel terminal, int left, int top)
    {
        fov.update(player.x, player.y, player.z, player.visionRadius());
        for (int x = 0; x < screenWidth; x++)
        {
            for (int y = 0; y < screenHeight; y++)
            {
                int wx = x + left;
                int wy = y + top;


                // draws what the tiles are as known to the world
                if (player.canSee(wx, wy, player.z))
                    terminal.write(world.glyph(wx, wy, player.z, player), x, y, world.color(wx, wy, player.z, player));
                // draws the tiles as they are known to the player (e.g. can be unknown)
                else
                    terminal.write(fov.tile(wx, wy, player.z).glyph(), x, y, Color.darkGray);

                //terminal.write(world.glyph(wx, wy, player.z, player), x, y, world.color(wx, wy, player.z, player));
            }
        }
    }

    // display and control our @ rather than scrolling on its own. Input makes the player move and then
    // calls the update function so each creature can move after that
    public Screen respondToUserInput(KeyEvent key)
    {
        // record the player’s level before doing anything else
        int level = player.level();


        if (subscreen != null) {
            subscreen = subscreen.respondToUserInput(key);
        } else {
            switch (key.getKeyCode()){
                //case KeyEvent.VK_ESCAPE: return new LoseScreen();
                //case KeyEvent.VK_ENTER: return new WinScreen();
                case KeyEvent.VK_LEFT:
                case KeyEvent.VK_H: player.moveBy(-1, 0, 0); break;
                case KeyEvent.VK_RIGHT:
                case KeyEvent.VK_L: player.moveBy( 1, 0, 0); break;
                case KeyEvent.VK_UP:
                case KeyEvent.VK_K: player.moveBy( 0,-1, 0); break;
                case KeyEvent.VK_DOWN:
                case KeyEvent.VK_J: player.moveBy( 0, 1, 0); break;
                case KeyEvent.VK_Y: player.moveBy(-1,-1, 0); break;
                case KeyEvent.VK_U: player.moveBy( 1,-1, 0); break;
                case KeyEvent.VK_B: player.moveBy(-1, 1, 0); break;
                case KeyEvent.VK_N: player.moveBy( 1, 1, 0); break;
                case KeyEvent.VK_D: subscreen = new DropScreen(player); break;
                case KeyEvent.VK_E: subscreen = new EatScreen(player); break;
                case KeyEvent.VK_Q: subscreen = new QuaffScreen(player); break;
                case KeyEvent.VK_W: subscreen = new EquipScreen(player); break;
                case KeyEvent.VK_X: subscreen = new ExamineScreen(player); break;
                case KeyEvent.VK_SEMICOLON: subscreen = new LookScreen(player, "Looking",
                        player.x - getScrollX(),
                        player.y - getScrollY()); break;
                case KeyEvent.VK_T: subscreen = new ThrowScreen(player,
                        player.x - getScrollX(),
                        player.y - getScrollY()); break;
                case KeyEvent.VK_F:
                    if (player.weapon() == null || player.weapon().rangedAttackValue() == 0)
                    player.notify("You don't have a ranged weapon equipped.");
                    else
                        subscreen = new FireWeaponScreen(player,
                                player.x - getScrollX(),
                                player.y - getScrollY()); break;
            }

            switch (key.getKeyChar()){
                case 'g':
                case ',': player.pickup(); break;
                case '<':
                    // if the player is trying to use an exit stairs - Our normal stair handling won’t work with up stairs on the uppermost layer of the world
                    if (userIsTryingToExit())
                        return userExits();
                    else
                        player.moveBy( 0, 0, -1);
                    break;
                case '>': player.moveBy( 0, 0, 1); break;
                case '?': subscreen = new HelpScreen(); break;
            }

            // After responding to the player’s input, we need to see if that resulted in a level up. If so, we
            // jump into a LevelUpScreen and tell it how many bonuses the player gets to pick.
            if (player.level() > level)
                subscreen = new LevelUpScreen(player, player.level() - level);

            if(player.hasItem("Battle Teddy of Destiny"))
            {
                victoryItemFound = true;
            }

            if(victoryItemFound && !rushEventHappened)
            {
                spawnGhouls();
                spawnZombies();
                player.notify("Enemies have been summoned to prevent your escape with the fabled weapon...");
                rushEventHappened = true;
            }
        }

        // tells the world to let everyone take a turn by calling the world update function which calls the update function of
        // all creatures only if there isn't a subscreen
        if (subscreen == null)
            world.update();

        // our player is in some danger of being killed, so this checks if they are alive after each update
        if (player.hp() < 1)
            return new LoseScreen();
        return this;
    }

    // checks if the player is trying to use an up stair on the top level
    private boolean userIsTryingToExit()
    {
        return player.z == 0 && world.tile(player.x, player.y, player.z) == Tile.EXIT_PORTAL;
    }

    private Screen userExits()
    {
        for (Item item : player.inventory().getItems()){

            if (item != null && item.isVictoryItem())
                return new WinScreen();
        }
        return new LoseScreen();
    }

    private void createCreatures(StuffFactory stuffFactory)
    {
        player = stuffFactory.newPlayer(messages, fov);

        for(int z = 0; z < world.depth(); z++) {
            for (int i = 0; i < 8; i++) {
                stuffFactory.newFungus(z);
            }
            for (int i = 0; i < 40; i++){
                stuffFactory.newBat(z);
            }
            // phantoms become more common on deeper levels
            for (int i = 0; i < z + 1; i++){
                for (int j = 0; j < 2; j++){
                    stuffFactory.newPhantom(z, player);
                }
            }

            // 50% chance of riftworms spawning on each level apart from the bottom level with the chamber - between 1 and 3 can spawn on a level
            if((int)(Math.random() * 100) >= 50)
            {
                if(z != world.depth() - 1)
                {
                    int numRiftWorms = (int)(Math.random() * 3) + 1;
                    for (int i = 0; i < numRiftWorms; i++) {
                        stuffFactory.newRiftWorm(z, player);
                    }
                }
            }

			for (int i = 0; i < z + 3; i++){
                stuffFactory.newZombie(z, player);
                stuffFactory.newGoblin(z, player);
                stuffFactory.newSkeleton(z, player);
            }
        }
    }

    // used to spawn ghouls when the player collects the victory item
    private void spawnGhouls()
    {
        for(int z = 0; z < world.depth()-1; z++) {
            for(int i = 0; i < 20; i++) {
                stuffFactory.newGhoul(z, player);
            }
        }
    }

    // used to spawn zombies when the player collects the victory item
    private void spawnZombies()
    {
        for(int z = 0; z < world.depth()-1; z++) {
            for(int i = 0; i < 20; i++) {
                stuffFactory.newZombie(z, player);
            }
        }
    }

    // create and add items to the world
    private void createItems(StuffFactory factory) {
        for (int z = 0; z < world.depth(); z++){
            for (int i = 0; i < world.width() * world.height() / 20; i++){
                factory.newRock(z);
            }

            int numLamps = (int)(Math.random() * 3) + 1;
            for (int i = 0; i < numLamps; i++){
                factory.newEtherealLamp(z);
            }
            // spawns healing herbs
            int numHerbs = (int)(Math.random() * 4) + 1;
            for(int i = 0; i < numHerbs; i++)
            {
                factory.newHealingHerb(z);
                factory.newJugOfWater(z);
            }
            factory.randomArmor(z);
            factory.randomWeapon(z);
            factory.randomWeapon(z);

            // potions become more common on deeper levels
            for (int i = 0; i < z + 1; i++){
                factory.randomPotion(z);
            }

            // 25% chance of dynamite spawning on each level
            if(Math.random() > 0.75)
            {
                factory.newDynamite(z);
                factory.newDynamite(z);
            }
        }

        // creates the keys needed to unlock the chamber containing the victory item
        factory.newYellowKey((int) (Math.random() * world.depth()));
        factory.newBlueKey((int) (Math.random() * world.depth()));
        factory.newGreenKey((int) (Math.random() * world.depth()));

        // 50% chance of a shovel spawning somewhere in the world
        int numShovels = (int)(Math.random() * 4);
        if(numShovels > 1)
        {
            factory.newGreenKey((int) (Math.random() * world.depth()));
        }

        // creates the victory item
        factory.newVictoryItem(world.depth()-1);

    }

    // the method to display all messages on the screen at once - it is called by the displayOutput method
    private void displayMessages(AsciiPanel terminal, List<String> messages)
    {
        int top = screenHeight - messages.size();
        for (int i = 0; i < messages.size(); i++){
            terminal.writeCenter(messages.get(i), top + i);
        }
        messages.clear();
    }

    // a helper method for display output that returns string representing the player's hunger
    private String hunger()
    {
        String hunger = "";
        if (player.food() < player.maxFood() * 0.1)
            return hunger + "Starving";
        else if (player.food() < player.maxFood() * 0.3)
            return hunger + "Hungry";

        else if (player.food() > player.maxFood() * 0.8)
            return hunger + "Full";

        else if (player.food() > player.maxFood() * 0.9)
            return hunger + "Stuffed";
        else
            return hunger + "Content";
    }

}

