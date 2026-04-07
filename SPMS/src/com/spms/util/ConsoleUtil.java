package com.spms.util;

public class ConsoleUtil {

    public static final String RESET  = "\u001B[0m";
    public static final String BOLD   = "\u001B[1m";
    public static final String CYAN   = "\u001B[36m";
    public static final String GREEN  = "\u001B[32m";
    public static final String RED    = "\u001B[31m";
    public static final String YELLOW = "\u001B[33m";

    public static void printHeader(String title) {
        int width = 60;
        String line = "=".repeat(width);
        System.out.println(CYAN + line + RESET);
        int pad = (width - title.length()) / 2;
        System.out.println(CYAN + " ".repeat(pad) + BOLD + title + RESET);
        System.out.println(CYAN + line + RESET);
    }

    public static void printSection(String title) {
        System.out.println("\n" + YELLOW + "--- " + title + " ---" + RESET);
    }

    public static void printSuccess(String msg) {
        System.out.println(GREEN + "[OK] " + msg + RESET);
    }

    public static void printError(String msg) {
        System.out.println(RED + "[ERROR] " + msg + RESET);
    }

    public static void printInfo(String msg) {
        System.out.println(CYAN + "[INFO] " + msg + RESET);
    }

    public static void printDivider() {
        System.out.println("-".repeat(60));
    }
}
