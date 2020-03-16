package logic;

import java.util.ArrayList;
import java.util.List;

public class Logic implements LogicInterface {

    private List<double[]> cop;

    public Logic() {
        this.cop = new ArrayList<>();
    }

    @Override
    public void addCopPoint(double tr, double tl, double br, double bl) {
        int L = 433; //wiiboard length
        int W = 228; // wiiboard width

        double xVal = (L/2) * (((tr+br)-(tl+bl))/(tr+br+tl+bl));
        double yVal = (W/2) * (((tr+tl)-(br+bl))/(tr+br+tl+bl));

        cop.add(new double[]{xVal,yVal});
    }

    @Override
    public double calculateTurningPointForMaintainingBalance() {
        return 0;
    }

    @Override
    public List getCopList() {
        return this.cop;
    }

    @Override
    public double calculateCurveLength() {
        return 0;
    }

    @Override
    public double calculateCurveArea() {
        return 0;
    }

    @Override
    public void clearData() {
        cop.clear();
    }


}
