package logic;

import java.util.List;

public interface LogicInterface {

    void addCopPoint(double x, double y, double time);

    double findTP();

    List getCopList();

    double calculateCurveLength();

    double calculateCurveArea();

    void clearData();

    String copToString();

    double calcCurveLengthX();

    double calcCurveLengthY();


}
