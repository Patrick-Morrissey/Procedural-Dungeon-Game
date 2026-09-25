// Name: Patrick Morrissey K00218348
// Date: 09/05/2026
// Function: CA3 Encounter Design and Tactical Play

package roguelike;

// Creates the tiles that the world is made of. It uses the builder pattern.
// To create a World builder, a world size is needed.
// Then you can call methods, in fluent style, to build up a world. Once you’ve specified how to build the world you want, you call
// the build method and you get a new World to play with.

import asciiPanel.AsciiPanel;

import java.util.*;

// it uses Random Room Placement to mark rectangular areas for Drunkard's Walk walkers to carve out
public class WorldBuilder
{
    private int width;
    private int height;
    private int depth;
    private Tile[][][] tiles;
    private int[][][] regions;
    private int nextRegion;
    private List<List<Point>> tilesByType;
    private List<Map<Integer, Region>> regionMapByLevelList;

    // The process is to fill the area with cave floors and walls at random then to smooth everything out by
    // turning areas with mostly neighboring walls into walls and areas with mostly neighboring
    // floors into floors.

    public WorldBuilder(int width, int height, int depth)
    {
        this.width = width;
        this.height = height;
        this.depth = depth;
        this.tiles = new Tile[width][height][depth];

        this.regions = new int[width][height][depth];
        this.nextRegion = 1;

        this.tilesByType = new ArrayList<>(); // used for storing points of each tile type in different sub lists
        this.regionMapByLevelList = new ArrayList<>(); // used to a store a list of maps for each level containing regions which in turn each contain a list of all point within them
        // - the method to build it is called by WorldBuilder createRegions
    }

    public World build()
    {
        return new World(tiles, regions, regionMapByLevelList);
    }

    // makes caves - amended for z levels
    public WorldBuilder makeCaves()
    {
        return randomizeTiles()
                .smooth(8)
                .createRegions()
                .connectRegions()
                .addExitStairs()
                .addChamber(depth-1)
                .addFire(0.05)
                .buildTileByTypeList(); // this is an example of chaining methods
    }

    // randomize the tiles - this uses Drunkard's Walk
    private WorldBuilder randomizeTiles()
    {
        // initialises all tiles to be walls
        makeAllTilesWall();

        int maxNumRooms = 20;
        int maxRoomWH = 15;
        int minRoomWH = 5;
        int roomBuffer = 1;

        // assign the floor tiles on each level
        for (int z = 0; z < depth; z++)
        {
            // makes rectangular rooms out of UNKNOWN tiles that drunkards walk will then carve through
            randomRoomPlacement(z,maxRoomWH, minRoomWH,maxNumRooms,roomBuffer); // calls drunkardsWalk()
        }
        return this; // this allows method chaining
    }

    // Repeat the smoothing process a couple of times and you have an interesting
    // mix of cave walls and floors.
    // needed as part of cellular automata
    private WorldBuilder smooth(int times) {

        for (int time = 0; time < times; time++) {

            Tile[][][] tiles2 = new Tile[width][height][depth];

            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    for (int z = 0; z < depth; z++) {

                        int floors = 0;
                        int rocks = 0;

                        Point p = new Point(x,y,z);

                        for (Point neighbor : p.neighbors8())
                        {
                            if (neighbor.x < 0 || neighbor.y < 0 || neighbor.x >= width || neighbor.y >= height)
                                continue;
                            if (tiles[neighbor.x][neighbor.y][neighbor.z] == Tile.FLOOR)
                                floors++;
                            else
                                rocks++;
                        }

                        tiles2[x][y][z] = floors >= rocks ? Tile.FLOOR : Tile.WALL;
                    }
                }
            }

            tiles = tiles2;
        }

        return this;
    }

    // this uses Cellular Automata Parameters
    private WorldBuilder addFire(double chance)
    {
        buildTileByTypeList();
        List<Point> floorTiles = new ArrayList<>();
        Tile floorTile = Tile.FLOOR;

        int tileTypeIndex = floorTile.ordinal();

        // put all the floor tiles in the world into a single list
        floorTiles.addAll(tilesByType.get(tileTypeIndex));

        for(Point p : floorTiles)
        {
            // Math.random generates a random number 0 and 1. Sets the tile to floor if true (less than 0.5) or wall if false
            if (Math.random() < chance) {
                tiles[p.x][p.y][p.z] = Tile.FIRE;
            }
        }

        buildTileByTypeList();
        return this;
    }

    private void randomRoomPlacement(int z, int maxRoomWH, int minRoomWH, int maxNumRooms, int roomBuffer)
    {
        int minMaxDiff = maxRoomWH - minRoomWH;

        int failedAttempts = 0;
        for (int i = 0; i < maxNumRooms; i++)
        {
            boolean validRoomMade = false;
            while(!validRoomMade && failedAttempts < 1000)
            {
                List<Point> wallsOnLevel = tilesOnLevelOfType(z, Tile.WALL);

                // picks a random starting point from the list of wall tiles on the level
                int startTileIndex = (int) (Math.random() * wallsOnLevel.size());
                Point roomStartCorner = wallsOnLevel.get(startTileIndex);

                // sets random values for the room width and height - these will be the confines for the walker in each room
                int roomW = minRoomWH + (int) (Math.random() * (minMaxDiff + 1));
                int roomH = minRoomWH + (int) (Math.random() * (minMaxDiff + 1));

                String dirFromStartPoint = "";

                boolean notOverlapping = false;
                int numOverlapChecks = 0;

                // loops until the rectangle goes in a direction from the starting point that doesn't overlap another rectangle and is within bounds
                while(!notOverlapping && numOverlapChecks < 100)
                {
                    int dirFromStartPointNum = (int) (Math.random() * 4);

                    switch (dirFromStartPointNum) {
                        case 0: dirFromStartPoint = "rd"; break;
                        case 1: dirFromStartPoint = "ru"; break;
                        case 2: dirFromStartPoint = "ld"; break;
                        case 3: dirFromStartPoint = "lu"; break;
                    }
                    notOverlapping = checkRoomOverlapOrBounds(roomStartCorner, roomW, roomH, roomBuffer, dirFromStartPoint);
                    numOverlapChecks++;
                }
                // makes the chosen rectangle out of UNKNOWN tiles
                if(notOverlapping)
                {
                    int tempRoomTileCount = carveTempRectRoom(roomStartCorner, roomW, roomH, dirFromStartPoint);
                    drunkardsWalk(z, tempRoomTileCount);
                    validRoomMade = true;
                }
                else {
                    validRoomMade = false;
                    failedAttempts++;
                }
            }
        }
    }

    private void drunkardsWalk(int z, int tempRoomTileCount)
    {
        // adds all wall tiles on the level into the wallsOnLevel list
        List<Point> wallsOnLevel = tilesOnLevelOfType(z, Tile.UNKNOWN);

        int startTileIndex = (int) (Math.random() * wallsOnLevel.size()); // picks a random index from the wall tiles list

        int x = wallsOnLevel.get(startTileIndex).x;
        int y = wallsOnLevel.get(startTileIndex).y;

        // make the starting tile a floor
        tiles[x][y][z] = Tile.FLOOR;

        int steps = 0;
        // the higher the count of steps increases, the larger the rooms become generally
        while (steps < tempRoomTileCount * 2)
        {
            boolean dirWithinBounds = false;

            // pick a random direction to go in that is within the map's bounds
            while (!dirWithinBounds)
            {
                int dir = (int) (Math.random() * 4); // picks a random direction (up, down, left, or right)
                int newX = x, newY = y;

                switch (dir)
                {
                    case 0: newY = y - 1; break;
                    case 1: newY = y + 1; break;
                    case 2: newX = x - 1; break;
                    case 3: newX = x + 1; break;
                }

                if ((newX < 0 || newX >= width || newY < 0 || newY >= height))
                {
                    dirWithinBounds = false;
                }
                else if(tiles[newX][newY][z] == Tile.WALL)
                {
                    dirWithinBounds = false;
                }
                else
                {
                    dirWithinBounds = true;
                    x = newX;
                    y = newY;
                }
            }

            if (tiles[x][y][z] == Tile.WALL || tiles[x][y][z] == Tile.UNKNOWN)
            {
                tiles[x][y][z] = Tile.FLOOR;
            }
            steps++;
        }

        resetUnknownTilesToWall();
        buildTileByTypeList();
    }

    public int carveTempRectRoom(Point start, int roomW, int roomH, String rectDir)
    {
        int tempRoomTileCount = 0;
        int x = start.x, y = start.y;

        if(x < 0 || y < 0 || x >= width || y >= height)
        {
            return 0;
        }
        if(Objects.equals(rectDir, "rd"))
        {
            // right and down
            for(x = start.x; x < start.x + roomW; x++)
            {
                for(y = start.y; y < start.y + roomH; y++)
                {
                    tiles[x][y][start.z] = Tile.UNKNOWN;
                    tempRoomTileCount++;
                }
            }
        }
        if(Objects.equals(rectDir, "ru"))
        {
            // right and up
            for(x = start.x; x < start.x + roomW; x++)
            {
                for(y = start.y; y > start.y - roomH; y--)
                {
                    tiles[x][y][start.z] = Tile.UNKNOWN;
                    tempRoomTileCount++;
                }
            }
        }
        if(Objects.equals(rectDir, "ld")) {
            // left and down
            for(x = start.x; x > start.x - roomW; x--)
            {
                for(y = start.y; y < start.y + roomH; y++)
                {
                    tiles[x][y][start.z] = Tile.UNKNOWN;
                    tempRoomTileCount++;
                }
            }
        }
        if(Objects.equals(rectDir, "lu"))
        {
            // left and up
            for(x = start.x; x > start.x - roomW; x--)
            {
                for(y = start.y; y > start.y - roomH; y--)
                {
                    tiles[x][y][start.z] = Tile.UNKNOWN;
                    tempRoomTileCount++;
                }
            }
        }
        buildTileByTypeList();
        return tempRoomTileCount;
    }

    public boolean checkRoomOverlapOrBounds(Point start, int roomW, int roomH, int buffer, String rectDir)
    {
        int x = start.x, y = start.y;
        int x1,x2,y1,y2;

        if(x < 0 || y < 0 || x >= width || y >= height) { return false ; }

        // checks the area of the rectangle plus a one tile wide buffer around the intended area for a temporary rectangle
        if(Objects.equals(rectDir, "rd"))
        {
            x1 = start.x - buffer;
            x2 = start.x + roomW + buffer;
            y1 = start.y - buffer;
            y2 = start.y + roomH + buffer;
            // right and down
            for(x = x1; x < x2; x++)
            {
                for(y = y1; y < y2; y++)
                {
                    if(x < 0 || y < 0 || x >= width || y >= height) { return false ; }
                    if(tiles[x][y][start.z] == Tile.UNKNOWN || tiles[x][y][start.z] == Tile.FLOOR) {return false;}
                }
            }
        }
        if(Objects.equals(rectDir, "ru"))
        {
            x1 = start.x - buffer;
            x2 = start.x + roomW + buffer;
            y1 = start.y + buffer;
            y2 = start.y - roomH - buffer;
            // right and up
            for(x = x1; x < x2; x++)
            {
                for(y = y1; y > y2; y--)
                {
                    if(x < 0 || y < 0 || x >= width || y >= height) { return false ; }
                    if(tiles[x][y][start.z] == Tile.UNKNOWN || tiles[x][y][start.z] == Tile.FLOOR) {return false;}
                }
            }
        }
        if(Objects.equals(rectDir, "ld"))
        {
            x1 = start.x + buffer;
            x2 = start.x - roomW - buffer;
            y1 = start.y - buffer;
            y2 = start.y + roomH + buffer;
            // left and down
            for(x = x1; x > x2; x--)
            {
                for(y = y1; y < y2; y++)
                {
                    if(x < 0 || y < 0 || x >= width || y >= height) { return false ; }
                    if(tiles[x][y][start.z] == Tile.UNKNOWN || tiles[x][y][start.z] == Tile.FLOOR) {return false;}
                }
            }
        }
        if(Objects.equals(rectDir, "lu"))
        {
            x1 = start.x + buffer;
            x2 = start.x - roomW - buffer;
            y1 = start.y + buffer;
            y2 = start.y - roomH - buffer;
            // left and up
            for(x = x1; x > x2; x--)
            {
                for(y = y1; y > y2; y--)
                {
                    if(x < 0 || y < 0 || x >= width || y >= height) { return false ; }
                    if(tiles[x][y][start.z] == Tile.UNKNOWN || tiles[x][y][start.z] == Tile.FLOOR) {return false;}
                }
            }
        }
        return true;
    }

    private void makeAllTilesWall()
    {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                for (int z = 0; z < depth; z++) {
                    tiles[x][y][z] = Tile.WALL;
                }
            }
        }
        buildTileByTypeList();
    }

    private void resetUnknownTilesToWall()
    {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                for (int z = 0; z < depth; z++) {
                    if(tiles[x][y][z] == Tile.UNKNOWN)
                        tiles[x][y][z] = Tile.WALL;
                }
            }
        }
        buildTileByTypeList();
    }

    // Then, in the WorldBuilder class, we create a region map. Each location has a number
    // that identifies what region of contiguous open space it belongs to; i.e. if two locations
    // have the same region number, then you can walk from one to the other without digging through walls.
    // If it is not a wall, and it does not have a region
    // assigned then that empty space, and all empty spaces it’s connected to, will be given a
    // new region number. If the region is to small it gets removed.
    private WorldBuilder createRegions()
    {
        regions = new int[width][height][depth];
        for (int z = 0; z < depth; z++)
        {
            for (int x = 0; x < width; x++)
            {
                for (int y = 0; y < height; y++){
                    // checks if not a wall, and it hasn't already been assigned a region id (id will equal 0 if unassigned)
                    if (tiles[x][y][z] != Tile.WALL && regions[x][y][z] == 0)
                    {
                        // gives it a region number and uses the fillRegion method to give all open tiles its connected to the same id
                        int size = fillRegion(nextRegion++, x, y, z);
                        // deletes regions that are too small
                        if (size < 20)
                            removeRegion(nextRegion - 1, z);
                    }
                }
            }
        }
        buildRegionMapByLevelList();
        return this;
        // once done, the regions array can be used to see if two tiles are part of the same open space
    }

    // zero’s out the region number and fills in the cave so it’s solid wall to avoid overly small areas
    private void removeRegion(int region, int z)
    {
        for (int x = 0; x < width; x++)
        {
            for (int y = 0; y < height; y++)
            {
                if (regions[x][y][z] == region)
                {
                    regions[x][y][z] = 0;
                    tiles[x][y][z] = Tile.WALL;
                }
            }
        }
    }

    // does a flood-fill starting with an open tile. It, and any open
    // tile it’s connected to, gets assigned the same region number. This is repeated until there
    // are no unassigned empty neighboring tiles.
    private int fillRegion(int region, int x, int y, int z)
    {
        int size = 1; // counts the starting tile
        ArrayList<Point> open = new ArrayList<Point>(); // the queue of tiles to explore
        open.add(new Point(x,y,z)); // adds the starting to the open list
        regions[x][y][z] = region; // marks the tile as part of region
        while (!open.isEmpty())
        {
            Point p = open.remove(0); // removes the first point in the open list because it is currently being checked - this is like breadth first search
            for (Point neighbor : p.neighbors8())
            {
                // needed to prevent out of bounds issues
                if (neighbor.x < 0 || neighbor.y < 0 || neighbor.x >= width || neighbor.y >= height)
                    continue;
                // skips if the neighbour tile already has a region id or is a wall tile
                if (regions[neighbor.x][neighbor.y][neighbor.z] > 0 || tiles[neighbor.x][neighbor.y][neighbor.z] == Tile.WALL)
                    continue;
                // otherwise, increment the region size, mark the tile as part of the same region, and add it to the open list so its neighbours can be checked
                size++;
                regions[neighbor.x][neighbor.y][neighbor.z] = region;
                open.add(neighbor);
            }
        }
        return size;
    }

    // To connect all the regions with stairs we just start at the top and connect them one layer at a time
    public WorldBuilder connectRegions()
    {
        for (int z = 0; z < depth-1; z++){
            connectRegionsDown(z);
        }

        // placing this after the loop to connect layers horizontally reduces the amount of stairs made
        for (int z = 0; z < depth; z++) {
            connectRegionsHorizontally(z);
        }

        /*
        for (int z = 0; z < depth; z++) {
            int count = floorCount(z);

            System.out.println("Level " + (z + 1) + " floor percentage: " + (double) count / (width * height));
        }

         */
        return this; // this allows method chaining
    }

    // To connect two adjacent layers we look at each region that sits above another region. If
    // they haven’t been connected then we connect them.
    private void connectRegionsDown(int z)
    {
        List<String> connected = new ArrayList<String>();
        for (int x = 0; x < width; x++){
            for (int y = 0; y < height; y++){
                String region = regions[x][y][z] + "," + regions[x][y][z+1]; // The region variable is just a way to uniquely combine two numbers into one.
                // contains() is a method used to check if a string contains a sequence of characters so if the list of connected regions strings
                // already contains the id currently in string region, its id is added to the string list and the connectRegionsDown() method is used
                // to connected vertically adjacent regions
                if (tiles[x][y][z] == Tile.FLOOR && tiles[x][y][z+1] == Tile.FLOOR && !connected.contains(region))
                {
                    connected.add(region);
                    connectRegionsDown(z, regions[x][y][z], regions[x][y][z+1]);
                }
            }
        }
    }

    // To connect two regions, we get a list of all the locations where one is directly above the other.
    // Then, based on how much area overlaps, we connect them with stairs going up and stairs going down.
    private void connectRegionsDown(int z, int r1, int r2){
        List<Point> candidates = findRegionOverlaps(z, r1, r2);

        // sublist index 0 = stairs down point, sublist index 1 = stairs up point,
        List<List<Point>> stairsCandidatesDownUp = new ArrayList<>();

        // exits the method early if the list of candidates is empty
        if (candidates.isEmpty())
            return;

        int maxStairsDownCount = 4;
        int levelStairsDownLimit;

        // if the number of candidates is less than maxStairCount, set levelStairLimit to the number of candidates
        if(candidates.size() < maxStairsDownCount)
        {
            levelStairsDownLimit = candidates.size();
        }
        else {
            levelStairsDownLimit = ((int) (Math.random() * maxStairsDownCount)) + 1;
        }

        int stairsCandidates = 0;
        do{
            Point p = candidates.remove(0);

            tiles[p.x][p.y][z] = Tile.STAIRS_DOWN; // makes a stairs going down from Tile
            tiles[p.x][p.y][z+1] = Tile.STAIRS_UP;

            stairsCandidates++;
        }
        while (candidates.size() / stairsCandidates > 250); // if there are 240 tiles in the region

        int numStairsDown = countStairsDownOnLevel(z);
        if(numStairsDown > levelStairsDownLimit)
        {
            int numStairsDownToRemove = numStairsDown - levelStairsDownLimit;
            removeExtraStairs(z, numStairsDownToRemove);
        }
    }

    // Find all points in region r1 directly below a point in region r2
    public List<Point> findRegionOverlaps(int z, int r1, int r2)
    {
        ArrayList<Point> candidates = new ArrayList<Point>();
        for (int x = 0; x < width; x++){
            for (int y = 0; y < height; y++){
                if (tiles[x][y][z] == Tile.FLOOR && tiles[x][y][z+1] == Tile.FLOOR && regions[x][y][z] == r1 && regions[x][y][z+1] == r2)
                {
                    candidates.add(new Point(x,y,z));
                }
            }
        }
        Collections.shuffle(candidates);
        return candidates;
    }

    // counts the number of stairs going down on a level
    private int countStairsDownOnLevel(int z)
    {
        int levelStairsDownCount = 0;
        for (int x = 0; x < width; x++)
        {
            for (int y = 0; y < height; y++)
            {
                if(tiles[x][y][z] == Tile.STAIRS_DOWN)
                {
                    levelStairsDownCount++;
                }
            }
        }
        return levelStairsDownCount;
    }

    private void removeExtraStairs(int z, int numStairsDownToRemove)
    {
        buildTileByTypeList();

        List<Point> stairsDownOnLevel = new ArrayList<>();
        Map<Integer, Region> regionsOnLevel = regionMapByLevelList.get(z);

        for (Map.Entry<Integer, Region> map : regionsOnLevel.entrySet())
        {
            Region r = map.getValue();
            List<Point> regionStairsDown = r.getPointsByType(tiles, Tile.STAIRS_DOWN); // get all stairs down in the region
            // add the contents of regionStairsDown into stairsDownOnLevel - source on how to add list contents into other lists
            // https://www.digitalocean.com/community/tutorials/merge-two-lists-in-java
            stairsDownOnLevel.addAll(regionStairsDown);
        }
        Collections.shuffle(stairsDownOnLevel);

        for (int i = 0; i < numStairsDownToRemove; i++)
        {
            Point p = stairsDownOnLevel.get(i);
            tiles[p.x][p.y][p.z] = Tile.FLOOR;
            tiles[p.x][p.y][p.z+1] = Tile.FLOOR;
        }
        buildTileByTypeList();
    }

    // returns the region ids of all regions on a level
    private List<Integer> getRegionsOnLevel(int z)
    {
        List<Integer> regionsOnLevel = new ArrayList<>();
        for(int x = 0; x < width; x++)
        {
            for(int y = 0; y < height; y++)
            {
                int region = regions[x][y][z];
                // if its region id is 0 or if it's a wall
                if(region == 0 || tiles[x][y][z] == Tile.WALL)
                {
                    continue;
                }
                // if the regionsOnLevel list is empty, add the current region
                if(regionsOnLevel.isEmpty())
                {
                    regionsOnLevel.add(region);
                    continue;
                }

                boolean regionAlreadyFound = false;
                // loop through the regionsOnLevel list and check if the current region id is already in the list
                for (Integer i : regionsOnLevel)
                {
                    // if the current position's region id is in the list skip the current iteration
                    if (region == i)
                    {
                        regionAlreadyFound = true;
                        break;
                    }
                }

                // add the region id to the list if it is not already in it
                if(!regionAlreadyFound)
                {
                    regionsOnLevel.add(region);
                }
            }
        }
        return regionsOnLevel;
    }

    public void connectRegionsHorizontally(int z)
    {
        int numRegionsOnLevel = regionMapByLevelList.get(z).size();

        // puts all the floor tile points in every region into an array for that region and puts those region arrays into an array
        List<List<Point>> corridorsStartAndEndPoints = new ArrayList<>();

        // puts a list into the corridorsStartAndEndPoints list of lists for each region on the level and adds two null points to each sublist
        // that will be replaced by start and end points if suitable ones are found later - sublist guide: index 0 = start point, index 1 = end point
        for (int i = 0; i < numRegionsOnLevel; i++)
        {
            corridorsStartAndEndPoints.add(new ArrayList<>());
            corridorsStartAndEndPoints.get(i).add(null);
            corridorsStartAndEndPoints.get(i).add(null);
        }

        // a map containing each region on the current level
        Map<Integer, Region> currentLevelRegionsMap = regionMapByLevelList.get(z);
        // this converts a map keys to a list - source: https://www.geeksforgeeks.org/java/conversion-of-java-maps-to-list/
        List<Integer> regionsOnLevelIDList = new ArrayList<>(currentLevelRegionsMap.keySet());
        Collections.sort(regionsOnLevelIDList); // sorts the regionIDs in order

        // loops through each region on the level
        for (int i = 0; i < numRegionsOnLevel;i++)
        {
            int r1PointRegionID = regionsOnLevelIDList.get(i);
            Region r1Region = currentLevelRegionsMap.get(r1PointRegionID);

            int r2PointRegionID;
            Region r2Region;

            List<Point> r1RegionPoints = r1Region.getRegionEdgePoints(); // get the edge points in r1Region

            int shortestDis = Integer.MAX_VALUE;
            int shortestDisOneAlreadyConnected = Integer.MAX_VALUE;
            int randPointIndex = (int) (Math.random() * r1RegionPoints.size()); // generates a random index from the list of points in the current region

            Point r1Point = r1RegionPoints.get(randPointIndex);
            Point r2Point;

            int closestR2PointIfEitherR1R2AlreadyConnectedID = 0;
            Point closestR2PointIfEitherR1R2AlreadyConnected = null;
            Region closestR2RegionIfEitherR1R2AlreadyConnected = null;

            int closestR2PointAnyR2RegionID = 0;
            Point closestR2PointAnyR2Region = null;
            Region closestR2RegionAnyR2Region = null;

            // Loops through all regions on the level that isn't the region that the corridor will start in to find the point
            // that is closest to it from the other regions
            for (int j = 0; j < numRegionsOnLevel;j++)
            {
                if(i == j) // if the r2 region being compared to the r1 region is the r1 region, skip this iteration
                {
                    continue;
                }
                int currentR2RegionID = regionsOnLevelIDList.get(j);
                Region currentR2Region = currentLevelRegionsMap.get(currentR2RegionID);

                // get the edge tiles in currentR2Region
                List<Point> currentR2RegionPoints = currentR2Region.getRegionEdgePoints();

                for(Point r2CurrentPoint : currentR2RegionPoints)
                {
                    // compares the distance between the current point being checked and the random point selected in the starting region for the corridor
                    // and marks the current point as the closest point if it has the shortest distance found
                    int distanceBetweenPoints = distanceBetweenPoints(r1Point.x, r1Point.y, r2CurrentPoint.x, r2CurrentPoint.y);

                    // find the closest point in the closest region
                    if (distanceBetweenPoints < shortestDis)
                    {
                        shortestDis = distanceBetweenPoints;
                        closestR2PointAnyR2RegionID = currentR2RegionID;
                        closestR2PointAnyR2Region = r2CurrentPoint;
                        closestR2RegionAnyR2Region = currentR2Region;
                    }

                    // gets the closest point in another region if its the first iteration of the main loop or if region 1 or 2 is already connected to the network both not both
                    if (distanceBetweenPoints < shortestDisOneAlreadyConnected)
                    {
                        // checks if the r1p and rp2 are both already marked to be connected to the network
                        boolean pointsAlreadyConnected = false;

                        // checks if r2Region is already connected to r1Region by checking r2Region's list of connectedRegionIDs to see if r1Region's id is already there
                        for (Integer connectedRegionID : currentR2Region.regionsConnectedIDs())
                        {
                            if (connectedRegionID == r1PointRegionID)
                            {
                                pointsAlreadyConnected = true;
                                break;
                            }
                        }

                        boolean r1RegionConnected = r1Region.isConnectedToLevelNetwork();
                        boolean r2RegionConnected = currentR2Region.isConnectedToLevelNetwork();

                        // only expand the network from already connected regions
                        // marks the points to be connected if both points aren't already connected, if only one is already connected to the network already
                        // or neither are connected to the network and it's the first iteration of the overall loop
                        if (!pointsAlreadyConnected && (r1RegionConnected ^ r2RegionConnected || (!r1RegionConnected && !r2RegionConnected && i == 0)))
                        {
                            shortestDisOneAlreadyConnected = distanceBetweenPoints;
                            closestR2PointIfEitherR1R2AlreadyConnectedID = currentR2RegionID;
                            closestR2PointIfEitherR1R2AlreadyConnected = r2CurrentPoint;
                            closestR2RegionIfEitherR1R2AlreadyConnected = currentR2Region;
                        }
                    }
                }
            }

            // if a suitable point wasn't found in a r2 region where either region r1 or r2 both but not both are connected to the network if the current iteration isn't the first one
            if (closestR2PointIfEitherR1R2AlreadyConnected == null)
            {
                //continue;
                r2PointRegionID = closestR2PointAnyR2RegionID;
                r2Point = closestR2PointAnyR2Region;
                r2Region = closestR2RegionAnyR2Region;
            }
            else {
                r2PointRegionID = closestR2PointIfEitherR1R2AlreadyConnectedID;
                r2Point = closestR2PointIfEitherR1R2AlreadyConnected;
                r2Region = closestR2RegionIfEitherR1R2AlreadyConnected;
            }
            // adds the random point in the region and the closest point found in the closest region to the corridorsStartAndEndPoints list
            corridorsStartAndEndPoints.get(i).set(0,r1Point); // sets the start point for the connection for this region to the corridorsStartAndEndPoints list of lists
            corridorsStartAndEndPoints.get(i).set(1,r2Point); // sets the end point for the connection for this region to the corridorsStartAndEndPoints list of lists

            // checks if r2PointRegionID is already in r1Region's list of connected regions ids and if it isn't its added
            if(!r1Region.regionsConnectedIDs().contains(r2PointRegionID))
            {
                r1Region.addRegionsConnectedIDs(r2PointRegionID);
            }
            // checks if r1PointRegionID is already in r2Region's list of connected regions ids and if it isn't its added
            if(!r2Region.regionsConnectedIDs().contains(r1PointRegionID))
            {
                r2Region.addRegionsConnectedIDs(r1PointRegionID);
            }

            /******** Marks the current region and the region chosen to connect to it as being in the network of regions if one of them is already connected to the network ********/
            if(i == 0) // on the first iteration
            {
                r1Region.modifyIsConnectedToLevelNetwork(true); // marks the current region as being connected to the network of regions
                r2Region.modifyIsConnectedToLevelNetwork(true); // marks the r2region as being connected to the network of regions
            }
            else
            {
                boolean r1Connected = r1Region.isConnectedToLevelNetwork();
                boolean r2Connected = r2Region.isConnectedToLevelNetwork();
                if(r1Connected || r2Connected)
                {
                    r1Region.modifyIsConnectedToLevelNetwork(true); // marks the current region as being connected to the network of regions
                    r2Region.modifyIsConnectedToLevelNetwork(true); // marks the r2region as being connected to the network of regions
                }
            }
        }

        // connects all regions on the current level
        for(int i = 0; i < corridorsStartAndEndPoints.size();i++)
        {
            // skip this iteration doesn't have a both a start and end point
            if (corridorsStartAndEndPoints.get(i).get(0) == null || corridorsStartAndEndPoints.get(i).get(1) == null)
            {
                continue;
            }

            Point r1CP = corridorsStartAndEndPoints.get(i).get(0);
            Point r2CP = corridorsStartAndEndPoints.get(i).get(1);

            makeCorridor(r1CP, r2CP);
        }
    }

    // makes corridors that move either vertically up and then horizontally or vice versa
    public void makeCorridor(Point r1, Point r2)
    {
        int x = r1.x;
        int y = r1.y;
        int z = r1.z;

        int targetX = r2.x;
        int targetY = r2.y;

        int targetRegionID = regions[r2.x][r2.y][r2.z];

        // decides if the corridors move either vertically up and then horizontally or vice versa
        boolean moveVerticalFirst = (Math.random() < 0.5); // if less than 0.5, this is true

        // loops until x and y equal target x and target y
        while(x != targetX || y != targetY)
        {
            if(moveVerticalFirst)
            {
                // target is n of current position
                if (y > targetY) {y--;}
                // target is s of current position
                else if (y < targetY) {y++;}
                // target is e of current position
                else if (x < targetX) {x++;}
                // target is w of current position
                else if (x > targetX) {x--;}
            }
            else
            {
                // target is e of current position
                if (x < targetX) {x++;}
                // target is w of current position
                else if (x > targetX) {x--;}
                // target is n of current position
                else if (y > targetY) {y--;}
                // target is s of current position
                else if (y < targetY) {y++;}
            }

            // stop once the target room has been reached by checking if the current positon of the corridor has the same regionID as the target position
            if(regions[x][y][z] == targetRegionID)
            {
                break;
            }

            // makes the current tile a floor if it is a wall
            if(tiles[x][y][z] == Tile.WALL)
            {
                tiles[x][y][z] = Tile.FLOOR;
            }
        }
    }

    public int distanceBetweenPoints(int xR1, int yR1, int xR2, int yR2)
    {
        int distX = xR2 - xR1;
        int distY = yR2 - yR1;

        double distance = Math.sqrt((distX * distX) + (distY * distY));

        return (int)distance;
    }

    // builds a list of maps containing region objects for each region that contains a list of all the points in that region - called by connectRegions
    private void buildRegionMapByLevelList()
    {
        regionMapByLevelList.clear();
        for (int z = 0; z < depth; z++)
        {
            // create the map - source of where I learned to use Maps (a HashMap) - https://www.geeksforgeeks.org/java/map-interface-in-java/
            Map<Integer, Region> regionPointsMap = new HashMap<>();
            regionMapByLevelList.add(regionPointsMap); // add the map to the list of maps

            // for every region, add a map element using the region id as the key and a new Region object to represent the region
            List<Integer> regionsOnlevel = getRegionsOnLevel(z);
            for (int i = 0; i < regionsOnlevel.size(); i++)
            {
                int currentRegionID = regionsOnlevel.get(i);
                regionPointsMap.put(currentRegionID, new Region(currentRegionID));
            }

            for (int x = 0; x < width; x++)
            {
                for (int y = 0; y < height; y++)
                {
                    int currentTileRegionID = regions[x][y][z]; // gets the regionID of the current tile
                    // gets the Region matching the regionID provided and uses Region's addTilePoint method to add a point to represent the current tile
                    if(currentTileRegionID >= 1)
                    {
                        regionPointsMap.get(currentTileRegionID).addTilePoint(new Point(x,y,z));
                    }
                }
            }
        }
    }

    private WorldBuilder buildTileByTypeList()
    {
        tilesByType.clear();
        // value() is used to get all enum elements as an array and length gets the length of an array
        int numTileTypes = Tile.values().length;

        // puts an array for each tile type into the tileByType list of lists
        for (int i = 0; i < numTileTypes; i++)
        {
            tilesByType.add(new ArrayList<>());
        }

        for (int x = 0; x < width; x++)
        {
            for (int y = 0; y < height; y++)
            {
                for (int z = 0; z < depth; z++)
                {
                    Tile currentTile = tiles[x][y][z];
                    // ordinal() returns the position of the tile in the tiles enum declaration and this is used to assign the tile to the current sub list for its type
                    // source of info - https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/lang/Enum.html#valueOf(java.lang.Class,java.lang.String)
                    tilesByType.get(currentTile.ordinal()).add(new Point(x,y,z));
                }
            }
        }
        return this;
    }

    private List<Point> tilesOnLevelOfType(int z, Tile typeTile)
    {
        List<Point> tilesOnLevelOfType = new ArrayList<>();
        int tileTypeIndex = typeTile.ordinal();

        for (int i = 0; i < tilesByType.get(tileTypeIndex).size();i++)
        {
            Point p = tilesByType.get(tileTypeIndex).get(i);
            if(p.z == z)
            {
                tilesOnLevelOfType.add(p);
            }
        }

        return tilesOnLevelOfType;
    }

    // exit stairs for the game
    private WorldBuilder addExitStairs()
    {
        int x = -1;
        int y = -1;
        do {
            x = (int)(Math.random() * width);
            y = (int)(Math.random() * height);
        }
        while (tiles[x][y][0] != Tile.FLOOR || regions[x][y][0] == 0);

        tiles[x][y][0] = Tile.EXIT_PORTAL;
        return this;
    }

    private List<Point> getCorridorConnectionPoints(Region region)
    {
        List<Point> pointsOnRegionBoundaryList = region.getRegionEdgePoints();

        List<Point> corridorConnectionPoints = new ArrayList<>();

        // if the current point isn't on the edge of the world and if the point north, south, east, or west of the current point's regionID is 0 and is floor,
        // it to the list of corridor connection points
        for(Point p : pointsOnRegionBoundaryList)
        {
            // north
            if(p.y > 0 && regions[p.x][p.y-1][p.z] == 0 && tiles[p.x][p.y-1][p.z] == Tile.FLOOR)
            {
                corridorConnectionPoints.add(p);
            }
            // south
            if(p.y < height -1 && regions[p.x][p.y+1][p.z] == 0 && tiles[p.x][p.y+1][p.z] == Tile.FLOOR)
            {
                corridorConnectionPoints.add(p);
            }
            // east
            if(p.x < width - 1 && regions[p.x+1][p.y][p.z] == 0 && tiles[p.x+1][p.y][p.z] == Tile.FLOOR)
            {
                corridorConnectionPoints.add(p);
            }
            // west
            if(p.x > 0 && regions[p.x-1][p.y][p.z] == 0 && tiles[p.x-1][p.y][p.z] == Tile.FLOOR)
            {
                corridorConnectionPoints.add(p);
            }
        }
        return corridorConnectionPoints;
    }

    private WorldBuilder addChamber(int zLevel)
    {
        Map<Integer, Region> levelRegionsMap = regionMapByLevelList.get(zLevel);

        List<Region> levelRegionsMapList = new ArrayList<>(levelRegionsMap.values());

        List<Region> regionsFewestCorridorConnections = new ArrayList<>();
        // finds the value of lowestNumConnectionPoints by looping through each region and saving the value of the one with the fewest corridor connection points
        int lowestNumConnectionPoints = Integer.MAX_VALUE;
        for (Region i : levelRegionsMapList)
        {
            if(getCorridorConnectionPoints(i).size() < lowestNumConnectionPoints)
            {
                lowestNumConnectionPoints = getCorridorConnectionPoints(i).size();
            }
        }

        // loops through the list and adds the regions that have a number of corridor connection points that matches lowestNumConnectionPoints
        for (Region i : levelRegionsMapList)
        {
            if(getCorridorConnectionPoints(i).size() == lowestNumConnectionPoints)
            {
                regionsFewestCorridorConnections.add(i);
            }
        }

        int randomIndexFromRegionsFewestCorridorConnections = (int)(Math.random() * regionsFewestCorridorConnections.size());

        Region chosenRegion = regionsFewestCorridorConnections.get(randomIndexFromRegionsFewestCorridorConnections);
        chosenRegion.setRegionType(Region.RegionType.CHAMBER); // updates the region to be a chamber region

        List<Point> corridorConnectionPoints = getCorridorConnectionPoints(chosenRegion);
        for (Point p : corridorConnectionPoints)
        {
            //System.out.println("Connection Point " + corridorConnectionPoints.indexOf(p) + ": " + p.x + "," + p.y + "," + p.z);
            tiles[p.x][p.y][p.z] = Tile.LOCKED_DOOR;
        }
        return this;
    }
}