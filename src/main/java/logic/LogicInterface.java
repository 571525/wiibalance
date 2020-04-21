package logic;

import java.util.List;

public interface LogicInterface {

    /**
     * Records a point
     * @param x
     * @param y
     * @param time
     */
    void addCopPoint(double x, double y, double time);

    /**
     * Finds the turning point for maintaining balance as described in the article by T. Aasen.
     *
     * @return TP, -1.0 if error
     */
    double findTP();

    /**
     * returns a list on the format [x,y,time]
     * @return List<List<Double>> with all COP recordings
     */
    List getCopList();

    /**
     * calculates the total curvelength
     * @return
     */
    double calculateCurveLength();

    /**
     * Finds a 95% confidense ellipse and returns the total area
     * @return
     */
    double calculateCurveArea();

    /**
     * clears the COP recordings
     */
    void clearData();


    /**
     * Finds the curvelength on the x-axis, that is the total distance travelled up and down the x-axis
     * @return
     */
    double calcCurveLengthX();

    /**
     * Finds the curvelength on the y-axis, that is the total distance travelled up and down the y-axis
     * @return
     */
    double calcCurveLengthY();

    List getTpCurve();

    List getMsdCurve();

    List getTimeSeries();



}
