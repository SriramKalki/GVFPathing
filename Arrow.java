import java.awt.*;
import java.awt.geom.AffineTransform;

public class Arrow
{
    private final Polygon arrowHead = new Polygon ();

    /**
     * Create an arrow.
     *
     * @see <a href="https://stackoverflow.com/questions/2027613/how-to-draw-a-directed-arrow-line-in-java">...</a>
     *
     * @param size Size of the arrow to draw.
     */
    public Arrow (int size)
    {
        // create a triangle centered on (0,0) and pointing right
        arrowHead.addPoint (size, 0);
        arrowHead.addPoint (-size, -size);
        arrowHead.addPoint (-size, size);
        //arrowHead.addPoint (0, 0); // Another style
    }

    /**
     * Draw the arrow at the end of a line segment. Drawing the line segment must be done by the caller, using whatever
     * stroke and color is required.
     */
    public void drawArrowHead (Graphics2D g, double x0, double y0, double x1, double y1)
    {
        final AffineTransform tx = AffineTransform.getTranslateInstance (x1, y1);
        tx.rotate (Math.atan2 (y1 - y0, x1 - x0));
        g.fill (tx.createTransformedShape (arrowHead));
    }
}