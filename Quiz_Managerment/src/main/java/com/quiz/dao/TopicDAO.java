package com.quiz.dao;

import com.quiz.database.DatabaseConnection;
import com.quiz.model.Topic;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object cho Topic
 */
public class TopicDAO {
    private Connection connection;

    public TopicDAO() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }

    // Lấy tất cả chủ đề
    public List<Topic> getAllTopics() {
        List<Topic> topics = new ArrayList<>();
        String sql = "SELECT t.*, s.name as subjectName FROM Topics t " +
                    "LEFT JOIN Subjects s ON t.subjectId = s.id " +
                    "ORDER BY s.name, t.name";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Topic topic = new Topic();
                topic.setId(rs.getInt("id"));
                topic.setName(rs.getString("name"));
                topic.setSubjectId(rs.getInt("subjectId"));
                
                if (rs.getString("subjectName") != null) {
                    com.quiz.model.Subject subject = new com.quiz.model.Subject();
                    subject.setId(rs.getInt("subjectId"));
                    subject.setName(rs.getString("subjectName"));
                    topic.setSubject(subject);
                }
                
                topics.add(topic);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return topics;
    }

    // Lấy chủ đề theo môn học
    public List<Topic> getTopicsBySubject(int subjectId) {
        List<Topic> topics = new ArrayList<>();
        String sql = "SELECT * FROM Topics WHERE subjectId = ? ORDER BY name";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, subjectId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Topic topic = new Topic();
                topic.setId(rs.getInt("id"));
                topic.setName(rs.getString("name"));
                topic.setSubjectId(rs.getInt("subjectId"));
                topics.add(topic);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return topics;
    }

    // Thêm chủ đề mới
    public boolean addTopic(Topic topic) {
        String sql = "INSERT INTO Topics (name, subjectId) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, topic.getName());
            stmt.setInt(2, topic.getSubjectId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Cập nhật chủ đề
    public boolean updateTopic(Topic topic) {
        String sql = "UPDATE Topics SET name = ?, subjectId = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, topic.getName());
            stmt.setInt(2, topic.getSubjectId());
            stmt.setInt(3, topic.getId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Xóa chủ đề
    public boolean deleteTopic(int topicId) {
        String sql = "DELETE FROM Topics WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, topicId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
