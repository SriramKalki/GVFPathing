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

    public double closestS(Vector2 point){
        int SAMPLE_SIZE = 100;
        double closestS = 0;

        for(int i = 0; i <= totalArcLength; i += SAMPLE_SIZE){
            if(pointAtArcLength(i).subtract(point).getMagSq() < pointAtArcLength(closestS).subtract(point).getMagSq()){
                closestS = i;
            }
        }

        return closestS;
    }
    public Vector2 pointAtArcLength(double s){
        int segment = 0; //segment index where point will be located
        for (Double arcLength : arcLengths) {
            if (arcLength > s) break;
            s -= arcLength;
            segment++;
        }

        return splines.get(segment).pointAtArcLength(s);

    }

    public Vector2 dPointAtArcLength(double s){
        int segment = 0; //segment index where point will be located
        for (Double arcLength : arcLengths) {
            if (arcLength > s) break;
            s -= arcLength;
            segment++;
        }

        return splines.get(segment).dPointAtArcLength(s);

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
