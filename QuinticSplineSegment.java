import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;

import java.util.ArrayList;

public class QuinticSplineSegment {
    Vector2 p0;
    Vector2 p1;

    double[] xCoeffs = new double[6];
    double[] yCoeffs = new double[6];

    double STEP_RESOLUTION = 0.01;
    public QuinticSplineSegment(Vector2 p0,Vector2 p1){
        this.p0 = p0;
        this.p1 = p1;

        RealMatrix xMatrix = generateCoeffs(MatrixUtils.createRealMatrix(new double[][]{
                {p0.getX()},{200},{200},{p1.getX()},{200},{200}
        }));


        RealMatrix yMatrix = generateCoeffs(MatrixUtils.createRealMatrix(new double[][]{
                {p0.getY()},{200},{200},{p1.getY()},{200},{200}
        }));

        for(int i=0; i < 6; i++){
            xCoeffs[i] = xMatrix.getEntry(i,0);
            yCoeffs[i] = yMatrix.getEntry(i,0);
        }
    }

    public RealMatrix generateCoeffs(RealMatrix constraints){
        RealMatrix convMatrix = MatrixUtils.createRealMatrix(new double[][]{
                {0,0,0,0,0,1},{0,0,0,0,1,0},{0,0,0,2,0,0},{1,1,1,1,1,1},{5,4,3,2,1,0},{20,12,6,2,0,0}
        });
        return MatrixUtils.inverse(convMatrix).multiply(constraints);
    }

    public Vector2 getPoint(double t){
        //if(t < 0 || t > 1) return null;

        double x = getX(t);
        double y = getY(t);

        return new Vector2(x,y);
    }

    public double getX(double t){
        double ans = 0;
        for(int i=0; i < 6; i++){
            ans += xCoeffs[i] * Math.pow(t,5-i);
        }
        return ans;
    }

    public double getY(double t){
        double ans = 0;
        for(int i=0; i < 6; i++){
            ans += yCoeffs[i] * Math.pow(t,5-i);
        }
        return ans;
    }



    public double getArcLength(double t){
        //from 0 to t
        double ans = 0;
        for(double i = 0; i < t; i += STEP_RESOLUTION){
            ans += STEP_RESOLUTION * Math.sqrt(getdX(i)*getdX(i) + getdY(i)*getdY(i));
        }
        return ans;

    }
    public Vector2 getdPoint(double t){
        if(t < 0 || t > 1) return null;

        double x = getdX(t);
        double y = getdY(t);

        return new Vector2(x,y);
    }

    public double getdX(double t){
        double ans = 0;
        for(int i=0; i < 5; i++){
            ans += xCoeffs[i] * (5-i)* Math.pow(t,4-i);
        }
        return ans;
    }

    public double getdY(double t){
        double ans = 0;
        for(int i=0; i < 5; i++){
            ans += yCoeffs[i] * (5-i) * Math.pow(t,4-i);
        }
        return ans;
    }



    public ArrayList<Vector2> getPoints(double step){
        ArrayList<Vector2> points = new ArrayList<>();

        for(double i=0; i <=1; i += step){
            points.add(getPoint(i));
        }

        return points;
    }

}
