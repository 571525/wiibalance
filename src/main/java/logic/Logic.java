package logic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
    public void addCopPoint(double xVal, double yVal, double time) {
        cop.add(Arrays.asList(xVal, yVal, time));
    }


    @Override
    public double findTP() {

        List<Double> timeSerie = getTimeSerie();
        List<List<Double>> slope = new ArrayList<>();


        int beta = 500;
        int n = timeSerie.size();
        int k = 1;
        double timeInterval = 0.0;
        double stepsize = cop.get(cop.size() - 1).get(2) / cop.size(); // i sekunder
        double sumSpread = 0.0;

        while (k < beta) {
            double xi, xiPlusK, sqrd;
            for (int i = 0; i < (-k + n); i++) {
                xi = timeSerie.get(i);
                xiPlusK = timeSerie.get(i + k);
                sqrd = Math.pow(xiPlusK - xi, 2);
                sumSpread += sqrd;
            }
            double msd = sumSpread / (-k + n);
            double hurst = 0.5 * Math.log10(msd);
            slope.add(Arrays.asList(Math.log10(timeInterval), hurst, timeInterval));
            k++;
            timeInterval += stepsize;
            sumSpread = 0.0;
        }

        double h1, h2, t1, t2, tp;
        for (int i = 5; i < slope.size(); i++) {
            h1 = slope.get(i).get(1);
            h2 = slope.get(i - 1).get(1);
            t1 = slope.get(i).get(0);
            t2 = slope.get(i - 1).get(0);
            tp = Math.abs((h1 - h2)) / (t1 - t2);
            if (tp <= 0.5) return slope.get(i).get(2);
        }

        return -1.0;

    }

    private List<Double> getTimeSerie() {
        List<Double> ts = new ArrayList<>();
        List<Double> z = new ArrayList<>();


        double x1, x2, y1, y2, sum;
        for (int i = 1; i < cop.size(); i++) {
            x1 = cop.get(i - 1).get(0);
            x2 = cop.get(i).get(0);
            y1 = cop.get(i - 1).get(1);
            y2 = cop.get(i).get(1);
            sum = Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2);
            z.add(Math.sqrt(sum));
        }

        double mean = z.stream().mapToDouble(a -> a).average().getAsDouble();

        List<Double> zMinMean = z.stream().map(a -> a - mean).collect(Collectors.toList());

        ts.add(z.get(0));

        double c, d, value;
        for (int i = 1; i < zMinMean.size(); i++) {
            c = zMinMean.get(i);
            d = ts.get(i - 1);
            value = c + d;
            ts.add(value);
        }

        return ts;
    }


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

            length += (x1 > x2) ? x1 - x2 : x2 - x1;
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

            length += (y1 > y2) ? y1 - y2 : y2 - y1;
        }

        return length;
    }
}
