/*
 * Airline Reservation System
 * Developed by Vamsi Ukkusuri
 * © 2026 All Rights Reserved
 */
package com.airline.util;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Professional colored logging system.
 * Writes detailed records to a local log file ('application.log') and colored alerts to the console.
 */
public class Logger {
    private static final String LOG_FILE = "application.log";
    private static final SimpleDateFormat TIMESTAMP_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    // ANSI Escape Color Codes
    private static final String RESET = "\u001B[0m";
    private static final String CYAN = "\u001B[36m";
    private static final String GREEN = "\u001B[32m";
    private static final String YELLOW = "\u001B[33m";
    private static final String RED = "\u001B[31m";
    private static final String PURPLE = "\u001B[35m";

    /**
     * Log an informational message.
     */
    public static void info(String message) {
        log("INFO", message, GREEN);
    }

    /**
     * Log a debug level message.
     */
    public static void debug(String message) {
        log("DEBUG", message, CYAN);
    }

    /**
     * Log a warning level message.
     */
    public static void warn(String message) {
        log("WARNING", message, YELLOW);
    }

    /**
     * Log an error.
     */
    public static void error(String message) {
        log("ERROR", message, RED);
    }

    /**
     * Log an error with exception stack details.
     */
    public static void error(String message, Throwable throwable) {
        log("ERROR", message + " | Exception: " + throwable.getMessage(), RED);
        writeStackTraceToFile(throwable);
    }

    private static void log(String level, String message, String color) {
        String timestamp = TIMESTAMP_FORMAT.format(new Date());
        String fileMessage = String.format("%s [%s] %s", timestamp, level, message);
        String consoleMessage = String.format("%s%s [%s] %s%s", color, timestamp, level, message, RESET);

        // Write to Console
        System.out.println(consoleMessage);

        // Write to File
        try (FileWriter fw = new FileWriter(LOG_FILE, true);
             PrintWriter pw = new PrintWriter(fw)) {
            pw.println(fileMessage);
        } catch (IOException e) {
            System.err.println("CRITICAL: Failed to write logs to " + LOG_FILE + ": " + e.getMessage());
        }
    }

    private static void writeStackTraceToFile(Throwable throwable) {
        try (FileWriter fw = new FileWriter(LOG_FILE, true);
             PrintWriter pw = new PrintWriter(fw)) {
            throwable.printStackTrace(pw);
        } catch (IOException e) {
            System.err.println("CRITICAL: Failed to write stack trace to file: " + e.getMessage());
        }
    }
}
