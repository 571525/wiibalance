package logic;

import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.List;

public class Filemanager {

    public static void writeToCSV(String testType, String id, int duration, double tp, double xLen, double yLen, double totLen, double area, List<List<Double>> cop, File file) throws IOException {

        String filename = String.format("%s_%s_%tc.csv", id, testType, new Date());

        FileWriter csvWriter = new FileWriter(file.getAbsolutePath() + "/" + filename);

        csvWriter.append(String.format("Id,%s\n", id));
        csvWriter.append(String.format("Duration,%d\n", duration));
        csvWriter.append(String.format("Test,%s\n", testType));
        csvWriter.append(String.format("TP,%s\n", String.valueOf(tp).replace(",",".")));
        csvWriter.append(String.format("Curvelength X,%s\n", String.valueOf(xLen).replace(",",".")));
        csvWriter.append(String.format("Curvelength Y,%s\n", String.valueOf(yLen).replace(",",".")));
        csvWriter.append(String.format("Total Curvelength,%s\n", String.valueOf(totLen).replace(",",".")));
        csvWriter.append(String.format("Area,%s\n\n", String.valueOf(area).replace(",",".")));

        csvWriter.append("COP readings\n");

        csvWriter.append("X,Y,time\n");

        for (List<Double> rowData : cop) {
            csvWriter.append(String.join(",", rowData.toString()).replace("[", "").replace("]", ""));
            csvWriter.append("\n");
        }

        csvWriter.flush();
        csvWriter.close();
    }

    public static File findDir(Stage stage) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File selectedDirectory = directoryChooser.showDialog(stage);

        if (selectedDirectory == null) {
            return null;
        } else {
            return selectedDirectory;
        }
    }
}
