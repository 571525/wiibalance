package logic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Logic implements LogicInterface {

    private List<List<Double>> cop;

    public Logic() {
        this.cop = new ArrayList<>();
    }

    private static List<List<Double>> subtractMean(List<List<Double>> data) {
        double meanX = data.stream().mapToDouble(a -> a.get(0)).average().getAsDouble();
        double meanY = data.stream().mapToDouble(a -> a.get(1)).average().getAsDouble();

        List<List<Double>> list = data.subList(0, data.size());
        list.forEach(a -> {
            a.set(0, a.get(0) - meanX);
            a.set(1, a.get(1) - meanY);
        });

        return list;
    }

    @Override
    public void addCopPoint(double tr, double tl, double br, double bl, double time) {
        int L = 433; //wiiboardStack length
        int W = 228; // wiiboardStack width

        double xVal = (L / 2) * (((tr + br) - (tl + bl)) / (tr + br + tl + bl));
        double yVal = (W / 2) * (((tr + tl) - (br + bl)) / (tr + br + tl + bl));

        cop.add(Arrays.asList(xVal, yVal, time));
    }

    @Override
    public double calculateTurningPointForMaintainingBalance() {

        // Simple check to see if COP har more than one object
        if(cop.size() < 1) return -1.0;

        List<List<Double>> slope = new ArrayList<>();

        int beta = 500;
        int n = cop.size();
        int k = 1;
        double timeInterval = 0.0;
        double stepsize = cop.get(n - 1).get(2) / n; // i sekunder
        double sum = 0.0;

        while (k < beta) {
            for (int i = 0; i < (-k + n); i++) {
                List<Double> xiPlusK = cop.get(i + k);
                List<Double> xi = cop.get(i);

                double xDif = xiPlusK.get(0) - xi.get(0);
                double yDif = xiPlusK.get(1) - xi.get(1);

                sum += Math.pow(xDif,2) + Math.pow(yDif, 2);
            }
            double msd = sum / (-k + n);
            double slopeK = (0.5) * ((Math.log10(msd)));
            slope.add(Arrays.asList(slopeK,timeInterval));
            k++;
            timeInterval += stepsize;
            sum = 0.0;
        }

        List<List<Double>> derivatedSlope = calcSlopeDerivated(slope);

        for(int i = 0; i<derivatedSlope.size(); i++){
            System.out.println("slope: " + derivatedSlope.get(i).get(0));
            if (derivatedSlope.get(i).get(0) <= 0.5)
                return derivatedSlope.get(i).get(1);
        }

        return -1.0;
    }

    private List<List<Double>> calcSlopeDerivated(List<List<Double>> data) {
        List<List<Double>> deriv = new ArrayList<>();

        double x1,x2,y1,y2;
        for (int i = 1; i < data.size(); i++) {
            x1 = data.get(i-1).get(1);
            x2 = data.get(i).get(1);
            y1 = data.get(i-1).get(0);
            y2 = data.get(i).get(0);
            deriv.add(Arrays.asList((y2-y1)/(x2-x1),data.get(i).get(1)));
        }

        return deriv;
    }

    /**
     * This gets the COP points. Each entry is on the format [x,y,time]
     *
     * @return A List with COP points in format [x,y,time]
     */
    @Override
    public List getCopList() {
        return this.cop;
    }

    @Override
    public double calculateCurveLength() {
        double curveLength = 0.0;

        for (int i = 1; i < cop.size(); i++) {
            List<Double> cops = cop.get(i);
            List<Double> copsPrev = cop.get(i - 1);
            double x1 = cops.get(0);
            double y1 = cops.get(1);
            double x2 = copsPrev.get(0);
            double y2 = copsPrev.get(1);

            curveLength += Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
        }

        return curveLength;
    }

    @Override
    public double calculateCurveArea() {

        List<List<Double>> subMean = subtractMean(cop);

        double Cxx = subMean.stream().mapToDouble(a -> a.get(0) * a.get(0)).sum();
        double Cxy = subMean.stream().mapToDouble(a -> a.get(0) * a.get(1)).sum();
        double Cyx = subMean.stream().mapToDouble(a -> a.get(1) * a.get(0)).sum();
        double Cyy = subMean.stream().mapToDouble(a -> a.get(1) * a.get(1)).sum();

        double eigenValue0 = ((Cxx + Cyy) / 2) + Math.sqrt(Math.pow(Cxy, 2) + Math.pow((Cxx - Cyy) / 2, 2));
        double eigenValue1 = ((Cxx + Cyy) / 2) - Math.sqrt(Math.pow(Cxy, 2) + Math.pow((Cxx - Cyy) / 2, 2));

        double stdDev0 = Math.sqrt(eigenValue0 / (cop.size() - 1));
        double stdDev1 = Math.sqrt(eigenValue1 / (cop.size() - 1));

        return 5.991 * Math.PI * stdDev0 * stdDev1;
    }

    @Override
    public void clearData() {
        cop.clear();
    }

    /**
     * Help method for logging content of COP list
     *
     * @return
     */
    @Override
    public String copToString() {
        StringBuilder s = new StringBuilder();

        cop.forEach(a -> s.append("ENTRY: " + a.get(0) + " " + a.get(1) + " " + a.get(2) + "\n"));

        return s.toString();
    }

    @Override
    public double calcCurveLengthX() {
        double length = 0.0;

        for (int i = 1; i < cop.size(); i++) {
            List<Double> cops = cop.get(i);
            List<Double> copsPrev = cop.get(i - 1);
            double x1 = Math.abs(cops.get(0));
            double x2 = Math.abs(copsPrev.get(0));

            if (x1 > x2)
                length += x1 - x2;
            else
                length += x2 - x1;
        }

        return length;
    }

    @Override
    public double calcCurveLengthY() {
        double length = 0.0;

        for (int i = 1; i < cop.size(); i++) {
            List<Double> cops = cop.get(i);
            List<Double> copsPrev = cop.get(i - 1);
            double y1 = Math.abs(cops.get(1));
            double y2 = Math.abs(copsPrev.get(1));

            if (y1 > y2)
                length += y1 - y2;
            else
                length += y2 - y1;
        }

        return length;
    }
}
