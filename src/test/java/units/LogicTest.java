package units;

import com.balance.logic.Logic;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

public class LogicTest {

    private Logic logic = new Logic();
    private File file;
    private List<Double> integratedCOP;
    private Scanner scanner;
    private Random random;

    @Before
    public void setup() {
        integratedCOP = new ArrayList<>();
        file = new File("src/test/java/units/true_values.txt");
        try {
            scanner = new Scanner(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        while (scanner.hasNextLine()) {
            String next = scanner.nextLine();
            double nextDouble = Double.parseDouble(next);
            integratedCOP.add(nextDouble);
        }

        random = new Random();
    }

    @Test
    public void testTPImplementation() {
        double tp = logic.findTP(integratedCOP);
        assertEquals(Math.round(tp * 100.0) / 100.0, 0.56);
    }

    @Test
    public void curvelengthXandYandTotalTest() {
        List<List<Double>> cList = new ArrayList<>();
        for (double i = 0; i <= 15; i++)
            cList.add(Arrays.asList(i, i));

        logic.setCopList(cList);

        double xLen = logic.calcCurveLengthX();
        double yLen = logic.calcCurveLengthY();
        double totLen = logic.calculateCurveLength();

        double trueX = 15;
        double trueY = 15;
        double trueTot = 15 * Math.sqrt(2);

        assertEquals(xLen, yLen);
        assertTrue(totLen > xLen);
        assertTrue(totLen > yLen);
        assertEquals(trueX, xLen);
        assertEquals(trueY, yLen);
        assertEquals(Math.round(trueTot * 1000) / 1000, Math.round(totLen * 1000) / 1000);
    }

    @Test
    public void testAreaLessThanMax() {
        List<List<Double>> cList = new ArrayList<>();
        double boundary = 15;
        double maxArea = Math.PI * Math.pow(boundary, 2);

        for (int i = 0; i < 5000; i++) {
            double x = random.nextDouble() * boundary;
            double y = random.nextDouble() * boundary;
            cList.add(Arrays.asList(x, y));
        }

        logic.setCopList(cList);
        double area = logic.calculateCurveArea();

        assertTrue(area > 0);
        assertTrue(maxArea >= area);
    }
}
