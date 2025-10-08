package com.quiz.dao;

import com.quiz.database.DatabaseConnection;
import com.quiz.model.Difficulty;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object cho Difficulty
 */
public class DifficultyDAO {
    private Connection connection;

    public DifficultyDAO() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }

    // Lấy tất cả difficulties
    public List<Difficulty> getAllDifficulties() {
        List<Difficulty> difficulties = new ArrayList<>();
        String sql = "SELECT * FROM Difficulties ORDER BY id";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Difficulty difficulty = new Difficulty();
                difficulty.setId(rs.getInt("id"));
                difficulty.setLevel(rs.getString("level"));
                difficulties.add(difficulty);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return difficulties;
    }

    // Lấy difficulty theo ID
    public Difficulty getDifficultyById(int id) {
        String sql = "SELECT * FROM Difficulties WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                Difficulty difficulty = new Difficulty();
                difficulty.setId(rs.getInt("id"));
                difficulty.setLevel(rs.getString("level"));
                return difficulty;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Thêm difficulty mới
    public boolean addDifficulty(Difficulty difficulty) {
        String sql = "INSERT INTO Difficulties (id, level) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, difficulty.getId());
            stmt.setString(2, difficulty.getLevel());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Cập nhật difficulty
    public boolean updateDifficulty(Difficulty difficulty) {
        String sql = "UPDATE Difficulties SET level = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, difficulty.getLevel());
            stmt.setInt(2, difficulty.getId());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Xóa difficulty
    public boolean deleteDifficulty(int id) {
        String sql = "DELETE FROM Difficulties WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
