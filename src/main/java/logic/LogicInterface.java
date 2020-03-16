package logic;

import java.util.List;

public interface LogicInterface {

    void addCopPoint(double tr, double tl, double br, double bl, double time);

    double calculateTurningPointForMaintainingBalance();

    List getCopList();

    double calculateCurveLength();

    double calculateCurveArea();

    void clearData();


}
