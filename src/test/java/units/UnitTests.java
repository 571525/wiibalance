package units;

import logic.Logic;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.TestCase.assertTrue;

public class UnitTests {

    private Logic logic = new Logic();

    @Before
    public void setup() {
        logic.addCopPoint(20,20,20,20,1);
        logic.addCopPoint(21,19,21,19,1);
        logic.addCopPoint(22,18,23,18,1);
        logic.addCopPoint(15,17,23,18,1);
        logic.addCopPoint(17,17,28,22,1);
        logic.addCopPoint(13,15,27,22,1);
    }

    @Test
    public void calculateCurveLength() {
        System.out.println(logic.calculateCurveLength());
        assertTrue(true);
    }

}
