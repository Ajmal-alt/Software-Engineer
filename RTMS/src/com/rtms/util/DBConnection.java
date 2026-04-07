package com.rtms.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static DBConnection instance;
    private Connection connection;
    private static final String URL  = "jdbc:mysql://localhost:3306/rtms_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASS = "root";

    private DBConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            this.connection = DriverManager.getConnection(URL, USER, PASS);
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC Driver not found.", e);
        }
    }

    public static DBConnection getInstance() throws SQLException {
        if (instance == null || instance.connection.isClosed()) {
            instance = new DBConnection();
        }
        return instance;
    }

    public Connection getConnection() { return connection; }

    public void closeConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) connection.close();
    }
}
