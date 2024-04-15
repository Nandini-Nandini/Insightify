package com.insightify.insightify_comparator.util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class Static_utils {
    // Log function to log errors
    public static void log(String error, String source) {
        try {
            FileWriter fileWriter = new FileWriter("database_handler.log", true);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(java.time.LocalDateTime.now() + " - " + source + " - " + error);
            bufferedWriter.newLine();
            bufferedWriter.close();
        } catch (IOException e) {
            System.out.println("Error writing to log file: " + e.getMessage());
        }
    }
}
