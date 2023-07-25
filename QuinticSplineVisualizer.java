import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;

public class QuinticSplineVisualizer extends JPanel implements MouseListener, MouseMotionListener {
    private int robotX,robotY;
    private double robotHeading;
    private boolean isDragging;
    static QuinticSpline splines;
    static ArrayList<Vector2> passPoints = new ArrayList<>();

    static ArrayList<Double> arcLengths = new ArrayList<>();
    static double totalArcLength = 0;
    
    public QuinticSplineVisualizer(){
        robotX = 500;
        robotY = 500;
        isDragging = false;
        addMouseListener(this);
        addMouseMotionListener(this);

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

        g.setColor(Color.BLACK);
        g.fillOval(robotX - 5, robotY - 5, 10, 10);
        
        
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

        g2d.setColor(Color.BLACK);
        for(int x=0; x <= 1000; x+= 50){
            for(int y = 0; y <= 1000; y+= 50){
                Vector2 currPoint = new Vector2(x,y);
                drawDirectionVector(g,currPoint,false);

            }
        }




        g2d.setColor(Color.MAGENTA);
        drawDirectionVector(g2d,new Vector2(robotX,robotY),true);


    }
    
    private void drawDirectionVector(Graphics g, Vector2 robotPos, boolean robot){
        int currX = (int) robotPos.getX();
        int currY = (int) robotPos.getY();
        Vector2 currPoint = new Vector2(robotPos.getX(),robotPos.getY());
        double closestT =  splines.closestT(currPoint);
        Vector2 closestPoint = splines.getPoint(closestT);
        Vector2 closestdPoint = splines.getdPoint(closestT);

        if(robot) g.fillOval((int) closestPoint.getX(), (int) closestPoint.getY(),20,20);

        Vector2 robotToClosestPoint = closestPoint.subtract(currPoint);
        double correctionFactor = Math.min(1, robotToClosestPoint.getMagnitude() / 100);
        double movementDirection = hlerp(closestdPoint.getHeading(), robotToClosestPoint.getHeading(), correctionFactor);
        robotHeading = movementDirection;
        
        int scale = robot ? 30 : 5;
        int dx = (int) (scale *  Math.cos(movementDirection));
        int dy = (int) (scale * Math.sin(movementDirection));
        if(robot) drawArrowLine(g, currX , currY, currX + dx, currY + dy,5,5);
        else drawArrowLine(g, currX - dx, currY - dy, currX + dx, currY + dy,5,5);
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

    private double lerp(double a, double b, double t) {
        return (1 - t) * a + t * b;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        int mouseX = e.getX();
        int mouseY = 1000 - e.getY();

        // Check if the mouse click is inside the point (consider a small bounding box around the point)
        if (mouseX >= robotX - 5 && mouseX <= robotX + 5 && mouseY >= robotY - 5 && mouseY <= robotY + 5) {
            isDragging = true;
        }else{
            int dx = (int) (10 *  Math.cos(robotHeading));
            int dy = (int) (10 *  Math.sin(robotHeading));
            System.out.println(dx);
            robotX += dx;
            robotY += dy;
            repaint();
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        isDragging = false;
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (isDragging) {
            robotX = e.getX();
            robotY = 1000 - e.getY();
            repaint(); // Redraw the point at the new location
        }
    }

    // Other mouse events we don't need to handle, but must implement
    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mouseMoved(MouseEvent e) {
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
