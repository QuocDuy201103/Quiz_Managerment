package com.quiz;

import com.quiz.database.DatabaseConnection;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Test kết nối database
 */
public class DatabaseTest {
    public static void main(String[] args) {
        System.out.println("=== TEST KẾT NỐI DATABASE ===");
        
        try {
            // Lấy instance database connection
            DatabaseConnection dbConnection = DatabaseConnection.getInstance();
            
            // Lấy connection
            Connection connection = dbConnection.getConnection();
            
            if (connection != null && !connection.isClosed()) {
                System.out.println("✅ Kết nối database THÀNH CÔNG!");
                System.out.println("Database URL: " + connection.getMetaData().getURL());
                System.out.println("Database Product: " + connection.getMetaData().getDatabaseProductName());
                System.out.println("Database Version: " + connection.getMetaData().getDatabaseProductVersion());
                
                // Test query đơn giản
                try {
                    var stmt = connection.createStatement();
                    var rs = stmt.executeQuery("SELECT 1 as test");
                    if (rs.next()) {
                        System.out.println("✅ Test query THÀNH CÔNG!");
                    }
                    rs.close();
                    stmt.close();
                } catch (SQLException e) {
                    System.err.println("❌ Test query THẤT BẠI: " + e.getMessage());
                }
                
            } else {
                System.err.println("❌ Kết nối database THẤT BẠI!");
            }
            
        } catch (Exception e) {
            System.err.println("❌ LỖI: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("=== KẾT THÚC TEST ===");
    }
}

