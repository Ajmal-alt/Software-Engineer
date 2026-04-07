package com.rtms.util;

public class ConsoleUtil {
    public static void printHeader(String title) {
        String line = "=".repeat(65);
        System.out.println(line);
        int pad = (65 - title.length()) / 2;
        if (pad < 0) pad = 0;
        System.out.println(" ".repeat(pad) + title);
        System.out.println(line);
    }
    public static void printSection(String title) { System.out.println("\n--- " + title + " ---"); }
    public static void printSuccess(String msg)   { System.out.println("[OK] " + msg); }
    public static void printError(String msg)     { System.out.println("[ERROR] " + msg); }
    public static void printInfo(String msg)      { System.out.println("[INFO] " + msg); }
    public static void printDivider()             { System.out.println("-".repeat(65)); }
}
