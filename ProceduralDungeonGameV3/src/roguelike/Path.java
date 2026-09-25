// Name: Patrick Morrissey K00218348
// Date: 09/05/2026
// Function: CA3 Encounter Design and Tactical Play

package roguelike;
import java.util.List;

// class that hides the pathfinding details from us

public class Path
{
    private static PathFinder pf = new PathFinder();
    private List<Point> points;
    public List<Point> points() { return points; }

    public Path(Creature creature, int x, int y)
    {
        points = pf.findPath(creature,
                new Point(creature.x, creature.y, creature.z),
                new Point(x, y, creature.z),
                300);
    }
}
