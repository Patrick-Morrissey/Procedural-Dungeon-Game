// Name: Patrick Morrissey K00218348
// Date: 09/05/2026
// Function: CA3 Encounter Design and Tactical Play

package roguelike;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

// a specific ai for the player that overrides the onEnter method in Creature to
// dig through walls and walk on ground tiles

// If your world has doors then you can make the player automatically open them by walking
// into them with code very similar to this.
public class PlayerAI extends CreatureAI
{
    // messages list is created in playscreen, passed to creature factory which then passes it to player ai
    private List<String> messages;
    private FieldOfView fov;

    public PlayerAI(Creature creature, List<String> messages, FieldOfView fov)
    {
        super(creature);
        this.messages = messages;
        this.fov = fov;
    }

    // Instead of checking the tile type directly we just ask if it can be walked on or dug through
    // with isGround and isDiggable in the Tile class.
    public void onEnter(int x, int y, int z, Tile tile)
    {
        if (tile.isGround()){
            creature.x = x;
            creature.y = y;
            creature.z = z;
        }
        else if (tile == Tile.LOCKED_DOOR)
        {
            creature.openLockedDoorOfChamber(x,y,z);
        }
        // allows the player to dig if they have a shovel and the tile is diggable
        else if (creature.hasShovelEquipped() && tile.isDiggable()) {
            creature.dig(x, y, z);
        }
    }

    // overrides the onNotify method in creatureAI and adds an message received to the playerAI list of messages
    public void onNotify(String message){
        messages.add(message);
    }

    // the player specific canSee method which uses the advanced field of view from the FieldOfView class
    public boolean canSee(int wx, int wy, int wz) {
        return fov.isVisible(wx, wy, wz);
    }

    // Override the onGainLevel method in CreatureAI so the player doesn't automatically get free bonuses
    public void onGainLevel(){}

    // the player override of rememberedTile
    public Tile rememberedTile(int wx, int wy, int wz) {
        return fov.tile(wx, wy, wz);
    }
}
