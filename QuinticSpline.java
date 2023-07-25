import java.util.ArrayList;

public class QuinticSpline {
    private ArrayList<QuinticSplineSegment> splines = new ArrayList<>();

    private ArrayList<Double> arcLengths = new ArrayList<>();
    private double totalArcLength = 0;

    public QuinticSpline(ArrayList<Vector2> points){
        for(int i=0; i < points.size() - 1; i++){
            splines.add(new QuinticSplineSegment(points.get(i),points.get(i+1)));
            arcLengths.add(splines.get(i).getArcLength(1));
            totalArcLength += arcLengths.get(i);
        }
    }

    public ArrayList<Vector2> getPoints(double step){
        ArrayList<Vector2> allPoints = new ArrayList<>();

        for(QuinticSplineSegment spline : splines){
            allPoints.addAll(spline.getPoints(0.01));
        }

        return allPoints;
    }


    public double closestT(Vector2 pos){
        double curr = 0;
        for(double i=0; i <= 1; i+=0.01){
            if(getPoint(i).subtract(pos).getMagSq() < getPoint(curr).subtract(pos).getMagSq()){
                curr = i;
            }
        }

        return curr;
    }


    public static boolean epsilonEquals(double val1, double val2){
        return Math.abs(val1 - val2) < 1e-6;
    }

    public Vector2 getPoint(double t){
        int segment = (int)(t / (1.0 / splines.size()));
        double inner = t % (1.0 / splines.size());
        //t is whole curve parameterization from 0 to 1

        if(segment >= splines.size()){
            segment = splines.size() - 1;
            inner = 1;
        }

        if(segment < 0){
            segment = 0;
            inner = 0;
        }



        return splines.get(segment).getPoint( t * splines.size() - segment);
    }

    public Vector2 getdPoint(double t){
        int segment = (int)(t / (1.0 / splines.size()));
        double inner = t % (1.0 / splines.size());
        //t is whole curve parameterization from 0 to 1

        if(segment >= splines.size()){
            segment = splines.size() - 1;
            inner = 1;
        }

        if(segment < 0){
            segment = 0;
            inner = 0;
        }



        return splines.get(segment).getdPoint( t * splines.size() - segment);
    }

    public ArrayList<QuinticSplineSegment> getSplines() {
        return splines;
    }

    public ArrayList<Double> getArcLengths() {
        return arcLengths;
    }

    public double getTotalArcLength() {
        return totalArcLength;
    }
}
