package com.spms.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Singleton Database Connection Manager.
 * Reads credentials from config/db.properties at runtime.
 */
public class DBConnection {

    private static DBConnection instance;
    private Connection connection;

    // Defaults – overridden by db.properties if present
    private static String URL      = "jdbc:mysql://localhost:3306/spms_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static String DB_USER  = "root";
    private static String DB_PASS  = "root";

    static {
        try {
            Properties props = new Properties();
            props.load(new FileInputStream("config/db.properties"));
            URL     = props.getProperty("db.url",      URL);
            DB_USER = props.getProperty("db.username", DB_USER);
            DB_PASS = props.getProperty("db.password", DB_PASS);
        } catch (IOException e) {
            System.out.println("[INFO] config/db.properties not found – using defaults.");
        }
    }

    private DBConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            this.connection = DriverManager.getConnection(URL, DB_USER, DB_PASS);
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC Driver not found. Ensure mysql-connector-java.jar is on classpath.", e);
        }
    }

    public static DBConnection getInstance() throws SQLException {
        if (instance == null || instance.connection.isClosed()) {
            instance = new DBConnection();
        }
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }

    public void closeConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
}
