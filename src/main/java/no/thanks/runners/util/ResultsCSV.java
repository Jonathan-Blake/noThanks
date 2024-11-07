package no.thanks.runners.util;

import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

public class ResultsCSV implements AutoCloseable {
    private static String[] headers;
    private final CSVWriter writer;
    private ColumnValueFunction[] columnValueFunctions;


    public ResultsCSV() throws IOException {
        File file = new File("ResultsCSV.csv");
        // create FileWriter object with file as parameter
        FileWriter outputfile = new FileWriter(file);

        writer = new CSVWriter(outputfile);
    }

    private String[] getHeaders() {
        return headers;
    }

    private String[] buildCSVString(Map<String, Double> currentBestimate, TestResults totalResults, Map<String, Double> gradient) {
        return Arrays.stream(columnValueFunctions).map(func -> func.accept(currentBestimate, totalResults, gradient)).toList().toArray(new String[columnValueFunctions.length]);
    }

    public void writeNext(Map<String, Double> currentBestimate, TestResults totalResults, Map<String, Double> gradient) {
        writer.writeNext(buildCSVString(currentBestimate, totalResults, gradient));
    }

    public void close() throws IOException {
        writer.close();
    }

    public void setColumns(String[] headers, ColumnValueFunction[] columnValueFunctions) {
        this.headers = headers;
        this.columnValueFunctions = columnValueFunctions;
    }

    public void ready() {
        writer.writeNext(headers);
    }

    @FunctionalInterface
    public interface ColumnValueFunction {
        String accept(Map<String, Double> currentBestimate, TestResults totalResults, Map<String, Double> gradient);
    }
}
