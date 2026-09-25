// Name: Patrick Morrissey K00218348
// Date: 09/05/2026
// Function: CA3 Encounter Design and Tactical Play

package roguelike;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


// We need a way to determine if something is in our line of sight. To do this we get all the
// points in between us and what we want to look at and see if any of them block our vision.
// For this, we can create a new Line class that uses Bresenham’s line algorithm to find all
// the points along the line. To make it more convenient to loop through the points in a line,
// we also make the class implement Iterable<Point>.

public class Line implements Iterable<Point>
{
    private List<Point> points;

    public Line(int x0, int y0, int x1, int y1)
    {
        points = new ArrayList<Point>();
        int dx = Math.abs(x1 - x0);
        int dy = Math.abs(y1 - y0);
        int sx = x0 < x1 ? 1 : -1;
        int sy = y0 < y1 ? 1 : -1;
        int err = dx - dy;
        while (true)
        {
            points.add(new Point(x0, y0, 0));
            if (x0 == x1 && y0 == y1)
                break;
            int e2 = err * 2;
            if (e2 > -dx) {
                err -= dy;
                x0 += sx;
            }
            if (e2 < dx) {
                err += dx;
                y0 += sy;
            }
        }
    }
    public List<Point> getPoints() {
        return points;
    }
    public Iterator<Point> iterator() {
        return points.iterator();
    }
}