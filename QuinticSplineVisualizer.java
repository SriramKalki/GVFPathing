import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class QuinticSplineVisualizer extends JPanel {
    static QuinticSpline splines;
    static ArrayList<Vector2> passPoints = new ArrayList<>();

    static ArrayList<Double> arcLengths = new ArrayList<>();
    static double totalArcLength = 0;
    public QuinticSplineVisualizer(){
        passPoints.add(new Vector2(200,200));
        passPoints.add(new Vector2(500,200));
        passPoints.add(new Vector2(200,800));
        splines = new QuinticSpline(passPoints);

        arcLengths = splines.getArcLengths();
        totalArcLength = splines.getTotalArcLength();
    }

    @Override
    protected void paintComponent(Graphics g){
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.translate(0,1000);
        g2d.scale(1,-1);
        g2d.setColor(Color.RED);
        for(Vector2 point : passPoints){
            g2d.fillOval((int) point.getX()-20, (int) point.getY()-20,20,20);
        }

        g2d.setColor(Color.BLUE);
        ArrayList<Vector2> drawPoints = splines.getPoints(0.01);
        for (int i = 0; i < drawPoints.size() - 1; i++) {

            int x0 = (int) drawPoints.get(i).getX();
            int y0 = (int) drawPoints.get(i).getY();
            int x1 = (int) drawPoints.get(i+1).getX();
            int y1 = (int) drawPoints.get(i+1).getY();

            g2d.drawLine(x0,y0,x1,y1);
        }

        for(int i = 0; i < totalArcLength; i += 50){
            Vector2 dPoint = splines.dPointAtArcLength(i);
            Vector2 dPointNorm = dPoint.scalarDivide(0.1 * dPoint.getMagnitude());
            Vector2 normal = Vector2.rotate(dPointNorm, Math.PI/2);

            Vector2 point = splines.pointAtArcLength(i);
            int x0 = (int) point.getX();
            int y0 = (int) point.getY();

            g2d.setColor(Color.BLUE);
            drawArrowLine(g2d,x0,y0,(int) (x0 + dPointNorm.getX()),(int) (y0 + dPointNorm.getY()),5,5);
            g2d.setColor(Color.RED);
            drawArrowLine(g2d,x0,y0,(int) (x0 + normal.getX()),(int) (y0 + normal.getY()),5,5);
        }

        g2d.setColor(Color.MAGENTA);
        for(int x=100; x <= 600; x+= 20){
            for(int y = 100; y <= 800; y+= 20){
                System.out.println(x + " " + y);
                Vector2 currPoint = new Vector2(x,y);
                double closestS =  splines.closestS(currPoint);
                Vector2 closestPoint = splines.pointAtArcLength(closestS);
                Vector2 closestdPoint = splines.dPointAtArcLength(closestS);

                Vector2 robotToClosestPoint = closestPoint.subtract(currPoint);
                double correctionFactor = Math.min(1, robotToClosestPoint.getMagnitude() / 100);
                double movementDirection = hlerp(closestdPoint.getHeading(), robotToClosestPoint.getHeading(), correctionFactor);

                int dx = (int) (10 *  Math.cos(movementDirection));
                int dy = (int) (10 * Math.sin(movementDirection));
                drawArrowLine(g2d, x - dx , y - dy, x + dx, y + dy,5,5);

            }
        }


    }


    private void drawArrowLine(Graphics g, int x1, int y1, int x2, int y2, int d, int h) {
        int dx = x2 - x1, dy = y2 - y1;
        double D = Math.sqrt(dx*dx + dy*dy);
        double xm = D - d, xn = xm, ym = h, yn = -h, x;
        double sin = dy / D, cos = dx / D;

        x = xm*cos - ym*sin + x1;
        ym = xm*sin + ym*cos + y1;
        xm = x;

        x = xn*cos - yn*sin + x1;
        yn = xn*sin + yn*cos + y1;
        xn = x;

        int[] xpoints = {x2, (int) xm, (int) xn};
        int[] ypoints = {y2, (int) ym, (int) yn};

        g.drawLine(x1, y1, x2, y2);
        g.fillPolygon(xpoints, ypoints, 3);
    }

    public double hlerp(double a, double b, double t) {
        double diff = b - a;
        diff %= 2 * Math.PI;
        if (Math.abs(diff) > Math.PI) {
            if (diff > 0) {
                diff -= 2 * Math.PI;
            } else {
                diff += 2 * Math.PI;
            }
        }
        return a + t * diff;
    }

    public static void main(String[] args){

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("QSV");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.add(new QuinticSplineVisualizer());
            frame.setSize(1000, 1000);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });

    }
}
