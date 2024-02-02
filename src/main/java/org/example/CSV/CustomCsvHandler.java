package org.example.CSV;
import com.opencsv.CSVWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class CustomCsvHandler {
    public String csvFileName;

    public CustomCsvHandler(String fileName) {
        this.csvFileName = fileName;
    }

    public void appendUserData(List<String[]> data) {
        try (CSVWriter writer = new CSVWriter(new FileWriter(csvFileName, true))) {
            writer.writeAll(data);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Cannot write to CSV file.");
        }
    }
}

//json bhi kr sakte h??
