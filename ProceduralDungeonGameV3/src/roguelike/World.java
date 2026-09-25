// Name: Patrick Morrissey K00218348
// Date: 09/05/2026
// Function: CA3 Encounter Design and Tactical Play

package roguelike;
import asciiPanel.AsciiPanel;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

// the world holds the caves walls and floors and other in game elements
public class World
{
    private Tile[][][] tiles;
    private int[][][] regions;

    private List<Map<Integer, Region>> regionMapByLevelList;
    public List<Map<Integer, Region>> regionMapByLevelList() { return regionMapByLevelList; }

    private List<Creature> creatures; // stores all the creatures that will be in the world

    private Item[][][] items; // allows one item per tile

    private int width;
    public int width() { return width; }

    private int height;
    public int height() { return height; }

    private int depth;
    public int depth() { return depth; }

    private int chamberRegionID;
    public int chamberRegionID() { return chamberRegionID; }

    public World(Tile[][][] tiles, int[][][] regions, List<Map<Integer, Region>> regionMapByLevelList)
    {
        this.tiles = tiles;

        this.regions = regions;
        this.regionMapByLevelList = regionMapByLevelList;
        setChamberRegionID();

        this.width = tiles.length;
        this.height = tiles[0].length;
        this.depth = tiles[0][0].length;
        this.creatures = new ArrayList<>(); // initializes the arraylist of creatures

        this.items = new Item[width][height][depth];

    }

    public Tile tile(int x, int y, int z)
    {
        // By checking for bounds here we don’t need to worry about out of bounds errors and check
        // everytime we ask the world about a location.
        if (x < 0 || x >= width || y < 0 || y >= height || z < 0 || z >= depth)
            return Tile.BOUNDS;
        else
            return tiles[x][y][z];
    }

    // if a creature or item is at the position, the creature glyph is returned, otherwise the tile glyph is returned
    public char glyph(int x, int y, int z, Creature player){
        Creature other = creature(x, y, z);
        if (other != null)
            // if the player doesn't have a torch equipped and the creature is invisible, the tile glyph is returned instead of the creature
            if(!player.hasTorchEquipped() && other.isInvisible())
            {
                return tile(x, y, z).glyph();
            }
            else {
                return other.glyph();
            }

        if (item(x,y,z) != null)
            return item(x,y,z).glyph();
        return tile(x, y, z).glyph();
    }

    // if a creature or item is at the position, the creature colour is returned, otherwise the tile colour is returned
    public Color color(int x, int y, int z, Creature player){
        Creature other = creature(x, y, z);
        // if the tile is a fire tile, and there is creature or item there, return a red colour to indicate burning
        if(tile(x,y,z).isFire())
        {
            if(other != null || item(x,y,z) != null)
            {
                return AsciiPanel.brightRed;
            }
        }
        if (other != null)
        {
            // if the player doesn't have a torch equipped and the creature is invisible, the tile colour is returned instead of the creature colour
            if(!player.hasTorchEquipped() && other.isInvisible())
            {
                return tile(x, y, z).color();
            }
            else {
                return other.color();
            }
        }
        if (item(x,y,z) != null)
            return item(x,y,z).color();
        return tile(x, y, z).color();
    }

    // creature calls this method in its method called dig - it allow us to dig into cave walls
    public void dig(int x, int y, int z)
    {
        // if a floor is diggable and the player moves into it, the tile is changed into a floor tile
        if (tile(x,y,z).isDiggable())
            setTileToFloor(x,y,z);
    }

    // dynamiteAttack() in Creature uses this method
    public void explode(int x, int y, int z)
    {
        // if a floor is explodable and its within a dynamite blast, the tile is changed into a floor tile
        if (tile(x,y,z).isExplodable())
            setTileToFloor(x,y,z);
    }

    public void putOutFire(int x, int y, int z)
    {
        // if a floor is a fire tile and its within a water jug splash, the tile is changed into a floor tile
        if (tile(x,y,z).isFire())
            setTileToFloor(x,y,z);
    }

    private void setTileToFloor(int x, int y, int z)
    {
        tiles[x][y][z] = Tile.FLOOR;
    }

    // open a locked door by changing it from a locked door tile to a floor tile
    public void openLockedDoor(int x, int y, int z)
    {
        if(tiles[x][y][z] == Tile.LOCKED_DOOR)
        {
            tiles[x][y][z] = Tile.FLOOR;
        }
    }

    public int getChamberRegionID()
    {
        return chamberRegionID;
    }

    public void setChamberRegionID()
    {
        chamberRegionID = getChamber().id();
    }

    public Region getChamber()
    {
        Region chamber;

        // loops through the list of maps for each level
        for (Map<Integer, Region> regions : regionMapByLevelList)
        {
            // iterating through the map of the regions on the level to find the region of the chamber
            // - source that showed how to iterate through a map - https://www.geeksforgeeks.org/java/map-interface-in-java/
            for (Map.Entry<Integer, Region> map : regions.entrySet())
            {
                Region r = map.getValue();
                if (r.regionType() == Region.RegionType.CHAMBER)
                {
                    chamber = r;
                    return chamber;
                }
            }
        }
        return null;
    }


    public boolean isInChamber(int x, int y, int z)
    {
        return regions[x][y][z] == chamberRegionID();
    }


    // StuffFactory calls this method when creating a player to spawn them at an empty location
    // where no walls or creatures already are
    public void addAtEmptyLocation(Creature creature, int z){
        int x;
        int y;
        if(creature.isPlayer())
        {
            do {
                x = (int)(Math.random() * width);
                y = (int)(Math.random() * height);
            }
            // ensures the player spawns in a room and not a locked chamber or corridor
            while (!tile(x,y,z).isGround() || tile(x,y,z).isStairsOrExitPortal() || isInChamber(x,y,z) || regions[x][y][z] == 0 || creature(x,y,z) != null);
        }
        else {
            do {
                x = (int) (Math.random() * width);
                y = (int) (Math.random() * height);
            }
            while (!tile(x, y, z).isGround() || tile(x,y,z).isStairsOrExitPortal() || creature(x, y, z) != null);
        }
        creature.x = x;
        creature.y = y;
        creature.z = z;
        creatures.add(creature);
    }

    // StuffFactory calls this method when creating an item to spawn it at an empty location
    public void addAtEmptyLocation(Item item, int depth) {
        // ensures the only item that spawns in the chamber is the victory item
        if(item.isVictoryItem())
        {
            List<Point> pointsInChamber = getChamber().tilePoints();
            Point p;
            do {
                p = pointsInChamber.get((int)(Math.random() * pointsInChamber.size())); // picks a random point from the points in the chamber
            }
            while (!tile(p.x,p.y,p.z).isGround() || tile(p.x,p.y,p.z).isStairsOrExitPortal() || item(p.x,p.y,p.z) != null);
            items[p.x][p.y][p.z] = item;
        }
        else
        {
            int x;
            int y;
            if(item.isKey()) // if the item is a key, place it anywhere but in a chamber
            {
                do {
                    x = (int)(Math.random() * width);
                    y = (int)(Math.random() * height);
                }
                while (!tile(x,y,depth).isGround() || tile(x,y,depth).isStairsOrExitPortal()|| isInChamber(x,y,depth)|| item(x,y,depth) != null);
            }
            else {
                do {
                    x = (int)(Math.random() * width);
                    y = (int)(Math.random() * height);
                }
                while (!tile(x,y,depth).isGround() || item(x,y,depth) != null);
            }
            items[x][y][depth] = item;
        }
    }

    // Adding an item to a specific place is more complicated since we only allow one item per
    // tile. Because of that, we need to check adjacent tiles for an open space and repeat until
    // we find one or run out of open spaces.
    /**** A funky side effect of this is that if there are no open spaces then the item won’t be
     added but will no longer be in the creature’s inventory - it will vanish from the game.
     You can either let that happen or somehow let the caller know that it hasn’t been added
     and shouldn’t be removed from the inventory
     ****/
    public boolean addAtEmptySpace(Item item, int x, int y, int z)
    {
        if (item == null)
            return false;
        List<Point> points = new ArrayList<Point>();
        List<Point> checked = new ArrayList<Point>();
        points.add(new Point(x, y, z));
        while (!points.isEmpty())
        {
            Point p = points.remove(0);
            checked.add(p);
            if (!tile(p.x, p.y, p.z).isGround())
                continue;
            if (items[p.x][p.y][p.z] == null)
            {
                items[p.x][p.y][p.z] = item;
                Creature c = this.creature(p.x, p.y, p.z);
                if (c != null)
                    c.notify("A %s lands between your feet.", item.name());
                return true;
            }
            else {
                List<Point> neighbors = p.neighbors8();
                neighbors.removeAll(checked);
                points.addAll(neighbors);
            }
        }
        return false; // returns false if no free space was found to drop the item
    }

    // gets the creature at a specify location or returns null
    public Creature creature(int x, int y, int z)
    {
        for (Creature c : creatures){
            if (c.x == x && c.y == y && c.z == z)
                return c;
        }
        return null;
    }

    // returns the item at a specific location
    public Item item(int x, int y, int z){
        return items[x][y][z];
    }

    // gets all the creatures
    public List<Creature> creatures(){
        return creatures;
    }

    // the method for moving a creature from the world - this is used by the creature modify health points method
    public void remove(Creature other) {
        creatures.remove(other);
    }

    // removes items from the world
    public void remove(int x, int y, int z) {
        items[x][y][z] = null;
    }

    // This allows us to remove an object even if we don’t know where it is.
    public void remove(Item item)
    {
        for (int x = 0; x < width; x++){
            for (int y = 0; y < height; y++){
                for (int z = 0; z < depth; z++){
                    if (items[x][y][z] == item) {
                        items[x][y][z] = null;
                        return;
                    }
                }
            }
        }
    }

    // the method for letting each creature know it's time to use their update method
    // a copy of the creature list is made here to avoid adding new creatures to the list while
    // looping through the same list
    public void update(){
        List<Creature> toUpdate = new ArrayList<Creature>(creatures);
        for (Creature creature : toUpdate){
            creature.update();
        }
    }

}