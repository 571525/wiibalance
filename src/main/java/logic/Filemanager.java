package logic;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class Filemanager {

    public static void writeToCSV(List<List<Double>> data, String path, String filename) throws IOException {

        FileWriter csvWriter = new FileWriter(path + "/" + filename);
        csvWriter.append("X");
        csvWriter.append(",");
        csvWriter.append("Y");
        csvWriter.append(",");
        csvWriter.append("tids interval");
        csvWriter.append("\n");

        for (List<Double> rowData : data) {
            csvWriter.append(String.join(",", rowData.toString()).replace("[", "").replace("]", ""));
            csvWriter.append("\n");
        }

        csvWriter.flush();
        csvWriter.close();
    }
}
