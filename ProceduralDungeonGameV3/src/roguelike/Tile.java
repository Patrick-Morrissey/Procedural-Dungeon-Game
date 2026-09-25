// Name: Patrick Morrissey K00218348
// Date: 09/05/2026
// Function: CA3 Encounter Design and Tactical Play

package roguelike;
import java.awt.Color;
import asciiPanel.AsciiPanel;

// For cave walls and floors
// Each Tile needs to be displayed so
// we need a glyph to display and a color to display it with. Since we only have a few different
// tile types, and all tiles of the same type look and behave the same, we can represent the
// tiles as a java enum.
public enum Tile
{
    FLOOR((char)250, AsciiPanel.yellow, "A dirt and rock cave floor."),
    WALL((char)177, AsciiPanel.yellow, "A dirt and rock cave wall."),
    // out of bounds - instead of having to always check if something is out of bounds before
    // checking the map about a specific tile, we can just ask and the map and it can tell us it’s
    // out of bounds.
    BOUNDS('x', AsciiPanel.brightBlack, "Beyond the edge of the world."),
    STAIRS_DOWN('>', AsciiPanel.white, "A stone staircase that goes down."),
    STAIRS_UP('<', AsciiPanel.white, "A stone staircase that goes up."),
    UNKNOWN(' ', AsciiPanel.white, "(unknown)"),
    LOCKED_DOOR((char)219, AsciiPanel.red, "A locked door."),// indicates a place that has not been seen
    EXIT_PORTAL('O', AsciiPanel.brightGreen, "A mystical portal leading out of this dungeon realm."),
    FIRE('*', new Color(255, 165, 0),"A burning fire.");

    private char glyph;

    public char glyph() {
        return glyph;
    }

    public void setGlyph(char glyph) { this.glyph = glyph; }

    private Color color;

    public Color color() {
        return color;
    }

    public void setColor(Color color) { this.color = color; }

    private String details;
    public String details(){ return details; }

    Tile(char glyph, Color color, String details) {
        this.glyph = glyph;
        this.color = color;
        this.details = details;
    }

    // This method is needed by the dig method in World
    // we don’t even have to know what the tile is we can just care about if it can be dug through. If we later add new
    // tiles, no-dig zones, or something else we just need to update this method.
    public boolean isDiggable()
    {
        // returns true if tile is a wall
        return this == Tile.WALL;
    }

    // This method is needed by the explode method in World
    public boolean isExplodable()
    {
        // returns true if tile is a wall
        return this == Tile.WALL;
    }

    public boolean isFire()
    {
        // returns true if tile is a fire tile
        return this == Tile.FIRE;
    }

    public boolean isGround()
    {
        // returns true if tile is not a wall or out of bounds
        return this != WALL && this != BOUNDS && this != LOCKED_DOOR;
    }
    public boolean isStairsOrExitPortal()
    {
        return this == STAIRS_DOWN || this == STAIRS_UP || this == EXIT_PORTAL;
    }

}