// Name: Patrick Morrissey K00218348
// Date: 09/05/2026
// Function: CA3 Encounter Design and Tactical Play

package roguelike;

// Let’s create a new class for our field of view. We can slightly extend the common definition
// of ours to not only determine what is in view but to remember what has already been seen
// too. What’s visible now and what was seen earlier are technically two different things and
// probably should be implemented by two different classes.

public class FieldOfView
{
    private World world;
    private int depth;

    private boolean[][] visible;
    // can be used to check if a tile is currently visible
    public boolean isVisible(int x, int y, int z){
        return z == depth && x >= 0 && y >= 0 && x < visible.length && y < visible[0].length && visible[x][y];
    }
    // stores what tiles have been seen
    private Tile[][][] tiles;
    // used for checking what type of tile was last seen somewhere
    public Tile tile(int x, int y, int z){
        return tiles[x][y][z];
    }

    public FieldOfView(World world)
    {
        this.world = world;
        this.visible = new boolean[world.width()][world.height()];
        this.tiles = new Tile[world.width()][world.height()][world.depth()];
        for (int x = 0; x < world.width(); x++){
            for (int y = 0; y < world.height(); y++){
                for (int z = 0; z < world.depth(); z++){
                    tiles[x][y][z] = Tile.UNKNOWN;

                }
            }
        }
    }

    // update what’s visible and has been seen - takes in the player position and vision radius
    public void update(int wx, int wy, int wz, int r){
        depth = wz;
        visible = new boolean[world.width()][world.height()];
        // checks a square area around the player
        for (int x = -r; x < r; x++){
            for (int y = -r; y < r; y++)
            {
                // trims the square area of vision into a circle based the vision radius
                if (x*x + y*y > r*r)
                    continue;
                // skip tiles that are out of bounds
                if (wx + x < 0 || wx + x >= world.width()
                        || wy + y < 0 || wy + y >= world.height())
                    continue;
                // loops until vision is blocked
                // makes each point in a line from the player until vision is blocked or the vision radius limit is reached
                for (Point p : new Line(wx, wy, wx + x, wy + y)){
                    Tile tile = world.tile(p.x, p.y, wz);
                    visible[p.x][p.y] = true;
                    tiles[p.x][p.y][wz] = tile;
                    if (!tile.isGround())
                        break;
                }
            }
        }
    }

}
