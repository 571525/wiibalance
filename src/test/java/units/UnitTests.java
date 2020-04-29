package units;

import com.bachelor.logic.Logic;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

public class UnitTests {

    private Logic logic = new Logic();
    private File file;
    private List<Double> integratedCOP;
    private Scanner scanner;

    @Before
    public void setup() {
        integratedCOP = new ArrayList<>();
        file = new File("/home/david/bachelor/WiiBalance/src/test/java/units/true_values.txt");
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


    }

    @Test
    public void testTPImplementation() {
        double tp = logic.findTP();
        System.out.println(tp);
        assertEquals(Math.round(tp * 100.0) / 100.0,0.56);
    }

}
