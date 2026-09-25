// Name: Patrick Morrissey K00218348
// Date: 09/05/2026
// Function: CA3 Encounter Design and Tactical Play

package roguelike.screens;
import java.awt.event.KeyEvent;
import roguelike.Creature;
import roguelike.Line;
import roguelike.Point;
import asciiPanel.AsciiPanel;

// We’ll let the user pick a tile and then
// tell them what it is. If you think about it, this isn’t the only time the user will pick
// a tile through. Throwing, firing bows, and aiming spells all involve picking a tile.
// Since the InventoryBasedScreen has payed off so well, we could create a TargetBasedScreen.

// We’ll keep track of the player, a caption representing what we’re targeting, the screen coordinates where the player is looking from, and the s and y offset of where we’re targeting.
// The player and caption are protected so our subclasses can use them.
// When it’s time to display the output, we need to draw a line from the player to the target.
// We also need to display the caption to the user

public abstract class TargetBasedScreen implements Screen
{
    protected Creature player;
    protected String caption;
    private int sx;
    private int sy;
    private int x;
    private int y;

    public TargetBasedScreen(Creature player, String caption, int sx, int sy)
    {
        this.player = player;
        this.caption = caption;
        this.sx = sx;
        this.sy = sy;
    }

    // creates a line from the player to the target
    public void displayOutput(AsciiPanel terminal)
    {
        for (Point p : new Line(sx, sy, sx + x, sy + y))
        {
            // prevents the line from being drawn outside the screen
            if (p.x < 0 || p.x >= 80 || p.y < 0 || p.y >= 24)
                continue;
            terminal.write('*', p.x, p.y, AsciiPanel.brightMagenta);
        }
        terminal.clear(' ', 0, 23, 80, 1);
        // writes the caption
        terminal.write(caption, 0, 23);
    }

    // The user can change what’s being targeted with the movement keys, select a target with
    // Enter, or cancel with Escape. If the user tries to target something it can’t, like firing out of
    // range, then we go back to where we were targeting before.
    public Screen respondToUserInput(KeyEvent key)
    {
        int px = x;
        int py = y;
        switch (key.getKeyCode()){
            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_H: x--; break;
            case KeyEvent.VK_RIGHT:
            case KeyEvent.VK_L: x++; break;
            case KeyEvent.VK_UP:
            case KeyEvent.VK_J: y--; break;
            case KeyEvent.VK_DOWN:
            case KeyEvent.VK_K: y++; break;
            case KeyEvent.VK_Y: x--; y--; break;
            case KeyEvent.VK_U: x++; y--; break;
            case KeyEvent.VK_B: x--; y++; break;
            case KeyEvent.VK_N: x++; y++; break;
            case KeyEvent.VK_ENTER: selectWorldCoordinate(player.x + x, player.y + y, sx + x, sy + y); return null;
            case KeyEvent.VK_ESCAPE: return null;
        }
        if (!isAcceptable(player.x + x, player.y + y)){
            x = px;
            y = py;
        }
        enterWorldCoordinate(player.x + x, player.y + y, sx + x, sy + y);
        return this;
    }

    // a simple method to determine if a tile is an acceptable target. Subclasses
    // can override this if they want something more specific.
    public boolean isAcceptable(int x, int y) {
        return true;
    }

    // After each time the target moves, we let subclasses do whatever they want, usually this
    // will be to update the caption or do nothing.
    public void enterWorldCoordinate(int x, int y, int screenX, int screenY) {
    }

    // moves the player once the user has selected a specific location
    public void selectWorldCoordinate(int x, int y, int screenX, int screenY){
    }


}