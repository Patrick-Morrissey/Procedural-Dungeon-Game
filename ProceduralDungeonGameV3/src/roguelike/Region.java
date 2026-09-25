// Name: Patrick Morrissey K00218348
// Date: 09/05/2026
// Function: CA3 Encounter Design and Tactical Play

package roguelike;

import java.util.ArrayList;
import java.util.List;

// stores region ids, a list of points representing tiles, a list of connected regionIDs, a boolean indicating if the region has been connected
// to the network of regions on its level, and an enum representing it type (e.g. normal room, chamber room)

public class Region
{
    private int id;
    public int id() { return id; }

    private List<Point> tilePoints;
    public List<Point> tilePoints(){ return tilePoints; }

    private List<Integer> regionsConnectedIDs;
    public List<Integer> regionsConnectedIDs(){ return regionsConnectedIDs; }

    private boolean isConnectedToLevelNetwork;
    public boolean isConnectedToLevelNetwork() { return isConnectedToLevelNetwork; }
    public void modifyIsConnectedToLevelNetwork(boolean connectionStatus) { isConnectedToLevelNetwork = connectionStatus; }

    public enum RegionType {NORMAL_ROOM, CHAMBER};
    private RegionType regionType;
    public RegionType regionType() { return regionType; }
    public void setRegionType(RegionType newRegionType) {regionType = newRegionType; }

    public Region(int id)
    {
        this.id = id;
        this.tilePoints = new ArrayList<>();
        this.regionsConnectedIDs = new ArrayList<>();
        this.isConnectedToLevelNetwork = false;
        this.regionType = RegionType.NORMAL_ROOM;
    }

    public void addTilePoint(Point tilePoint)
    {
        tilePoints.add(tilePoint);
        tilePoint.setRegionID(id);
    }

    public void addRegionsConnectedIDs(int connectedRegionID)
    {
        regionsConnectedIDs.add(connectedRegionID);
    }

    public int getSize()
    {
        return tilePoints.size();
    }

    public int getNumConnectedRegions()
    {
        return regionsConnectedIDs.size();
    }

    public List<Point> getPointsByType(Tile[][][] tiles, Tile tileType)
    {
        List<Point> pointsOfType = new ArrayList<>();
        int requiredTypeIOrdinal = tileType.ordinal();

        for(Point p : tilePoints)
        {
            int pointTileOrdinal = p.getPointTile(tiles).ordinal();
            if(pointTileOrdinal == requiredTypeIOrdinal)
            {
                pointsOfType.add(p);
            }
        }
        return pointsOfType;
    }



    // returns the edge points in the region
    public List<Point> getRegionEdgePoints()
    {
        List<Point> regionEdgePointsList = new ArrayList<>();

        // loops through all points in the region
        for(Point p : tilePoints)
        {
            // if the point to the north, south, east, and west of point p are all have coordinates that match a point in the region, point p is
            // considered an inside tile and this iteration is skipped, otherwise the point is added to the list of regionEdge points
            Point n = new Point(p.x,p.y-1,p.z);
            Point s = new Point(p.x,p.y+1,p.z);
            Point e = new Point(p.x+1,p.y,p.z);
            Point w = new Point(p.x-1,p.y,p.z);

            boolean northInRegion = false;
            boolean southInRegion = false;
            boolean eastInRegion = false;
            boolean westInRegion = false;

            // checks if the point north, south, east, or west of point p matches a point that is already in the list of points for the region
            // if the point in all 4 directions from point p is in the region's points list then it means the point is not an edge point
            for(Point p2 : tilePoints)
            {
                if(n.x == p2.x && n.y == p2.y)
                {
                    northInRegion = true;
                }
                if(s.x == p2.x && s.y == p2.y)
                {
                    southInRegion = true;
                }
                if(e.x == p2.x && e.y == p2.y)
                {
                    eastInRegion = true;
                }
                if(w.x == p2.x && w.y == p2.y)
                {
                    westInRegion = true;
                }
            }

            boolean tileIsInsideTile = northInRegion && southInRegion && eastInRegion && westInRegion;

            if(!tileIsInsideTile)
            {
                regionEdgePointsList.add(p);
            }
        }
        return regionEdgePointsList;
    }
}
