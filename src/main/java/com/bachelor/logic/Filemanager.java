package com.bachelor.logic;

import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import java.io.*;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class Filemanager {

    public static void writeToCsv(String testType, String id, int duration, double tp, double xLen, double yLen, double totLen, double area, List<List<Double>> cop, File file) throws IOException {

        String filename = String.format("%s_%s_%tc.csv", id, testType, new Date());
        FileWriter csvWriter = new FileWriter(file.getAbsolutePath() + "/" + filename);

        csvWriter.append(String.format("Id,%s\n", id));
        csvWriter.append(String.format("Duration,%d\n", duration));
        csvWriter.append(String.format("Test,%s\n", testType));
        csvWriter.append(String.format("TP,%s\n", String.valueOf(tp).replace(",", ".")));
        csvWriter.append(String.format("Curvelength X,%s\n", String.valueOf(xLen).replace(",", ".")));
        csvWriter.append(String.format("Curvelength Y,%s\n", String.valueOf(yLen).replace(",", ".")));
        csvWriter.append(String.format("Total Curvelength,%s\n", String.valueOf(totLen).replace(",", ".")));
        csvWriter.append(String.format("Area,%s\n\n", String.valueOf(area).replace(",", ".")));
        csvWriter.append("COP readings\n");
        csvWriter.append("X,Y,time\n");

        for (List<Double> rowData : cop) {
            csvWriter.append(String.join(",", rowData.toString()).replace("[", "").replace("]", ""));
            csvWriter.append("\n");
        }

        csvWriter.flush();
        csvWriter.close();
    }

    public static void writeAllToCsv(List<HashMap<String, Object>> data, File file, String id, String type, int duration) throws IOException {
        String filename = String.format("%s_%s_%tc.csv", id, type, new Date());
        FileWriter csvWriter = new FileWriter(file.getAbsolutePath() + "/" + filename);
        int inputs = data.size();

        //Start with id and testtype in first two rows
        csvWriter.append(String.format("Id,%s\n", id));
        csvWriter.append(String.format("Test type,%s\n", type));
        csvWriter.append(String.format("Duration,%d\n", duration));
        csvWriter.append("\n");

        csvWriter.append("Test,");
        for (int i = 1; i <= inputs; i++)
            csvWriter.append(i + ",");
        csvWriter.append("\n");

        csvWriter.append("TP,");
        for (int i = 0; i < inputs; i++)
            csvWriter.append(data.get(i).get("tp") + ",");
        csvWriter.append("\n");

        csvWriter.append("Area,");
        for (int i = 0; i < inputs; i++)
            csvWriter.append(data.get(i).get("area") + ",");
        csvWriter.append("\n");

        csvWriter.append("Curvelength,");
        for (int i = 0; i < inputs; i++)
            csvWriter.append(data.get(i).get("curvelength") + ",");
        csvWriter.append("\n");

        csvWriter.append("Curvelength X,");
        for (int i = 0; i < inputs; i++)
            csvWriter.append(data.get(i).get("curveX") + ",");
        csvWriter.append("\n");

        csvWriter.append("Curvelength Y,");
        for (int i = 0; i < inputs; i++)
            csvWriter.append(data.get(i).get("curveY") + ",");
        csvWriter.append("\n");

        for (int i = 1; i <= inputs; i++)
            csvWriter.append(String.format("Time %d, X %d, Y %d,", i, i, i));
        csvWriter.append("\n");

        int length = findMaxLength(data);
        for (int row = 0; row < length; row++) {
            for (int i = 0; i < inputs; i++) {
                List<List<Double>> list = (List<List<Double>>) data.get(i).get("cop");
                try {
                    List<Double> rowI = list.get(row);
                    double x = rowI.get(0);
                    double y = rowI.get(1);
                    double t = rowI.get(2);
                    csvWriter.append(t + "," + x + "," + y + ",");
                } catch (Exception e) {
                    csvWriter.append(",,,");
                }
            }
            csvWriter.append("\n");
        }
    }

    private static int findMaxLength(List<HashMap<String, Object>> data) {
        int max = 0;

        for (int i = 0; i < data.size(); i++) {
            List<List<Double>> l = (List<List<Double>>) data.get(i).get("cop");
            if (l.size() > max) max = l.size();
        }
        return max;
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
