package com.quiz.database;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Class quản lý kết nối database SQL Server
 */
public class DatabaseConnection {
    private static String DB_URL;
    private static String DB_USER;
    private static String DB_PASSWORD;
    
    static {
        loadConfig();
    }
    
    private static void loadConfig() {
        Properties props = new Properties();
        try (InputStream input = DatabaseConnection.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                System.err.println("Cannot find file config.properties, use default config");
                DB_URL = "jdbc:sqlserver://localhost:1433;databaseName=quanlytracnghiem;encrypt=false;trustServerCertificate=true;characterEncoding=UTF-8;useUnicode=true";
                DB_USER = "admin";
                DB_PASSWORD = "12345";
                return;
            }
            
            props.load(input);
            DB_URL = props.getProperty("db.url", "jdbc:sqlserver://localhost:1433;databaseName=quanlytracnghiem;encrypt=false;trustServerCertificate=true;characterEncoding=UTF-8;useUnicode=true");
            DB_USER = props.getProperty("db.username", "admin");
            DB_PASSWORD = props.getProperty("db.password", "12345");
        } catch (IOException e) {
            System.err.println("Error read file config: " + e.getMessage());
            DB_URL = "jdbc:sqlserver://localhost:1433;databaseName=quanlytracnghiem;encrypt=false;trustServerCertificate=true;characterEncoding=UTF-8;useUnicode=true";
            DB_USER = "admin";
            DB_PASSWORD = "12345";
        }
    }
    
    private static DatabaseConnection instance;
    private Connection connection;

    private DatabaseConnection() {
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            System.out.println("Connect database successfully!");
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("Error connect to database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static DatabaseConnection getInstance() {
        if (instance == null) {
            synchronized (DatabaseConnection.class) {
                if (instance == null) {
                    instance = new DatabaseConnection();
                }
            }
        }
        return instance;
    }

    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi tạo kết nối mới: " + e.getMessage());
            e.printStackTrace();
        }
        return connection;
    }

    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Đã đóng kết nối database");
            }
        } catch (SQLException e) {
            System.err.println("Lỗi đóng kết nối: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
