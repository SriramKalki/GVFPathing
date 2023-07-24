import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;

public class CubicBezierCurve extends JPanel {

    // Control points for the Bezier curve
    private Point2D.Double p0, p1, p2, p3;

    public CubicBezierCurve() {
        // Set the control points
        p0 = new Point2D.Double(50, 200);
        p1 = new Point2D.Double(150, 100);
        p2 = new Point2D.Double(250, 300);
        p3 = new Point2D.Double(350, 200);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // Set rendering hints for smoother curves
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw control points
        g2d.setColor(Color.RED);
        g2d.fillOval((int) p0.x, (int) p0.y, 8, 8);
        g2d.fillOval((int) p1.x, (int) p1.y, 8, 8);
        g2d.fillOval((int) p2.x, (int) p2.y, 8, 8);
        g2d.fillOval((int) p3.x, (int) p3.y, 8, 8);

        // Draw the Bezier curve
        g2d.setColor(Color.BLUE);
        Path2D.Double path = new Path2D.Double();
        path.moveTo(p0.x, p0.y);
        path.curveTo(p1.x, p1.y, p2.x, p2.y, p3.x, p3.y);
        g2d.draw(path);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Bezier Curve Example");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.add(new CubicBezierCurve());
            frame.setSize(500, 400);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
