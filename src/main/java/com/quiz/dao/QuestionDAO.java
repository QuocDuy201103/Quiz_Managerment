package com.quiz.dao;

import com.quiz.database.DatabaseConnection;
import com.quiz.model.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object cho Question
 */
public class QuestionDAO {
    private Connection connection;

    public QuestionDAO() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }

    // Lấy tất cả câu hỏi
    public List<Question> getAllQuestions() {
        List<Question> questions = new ArrayList<>();
        String sql = "SELECT q.*, t.name as topicName, d.level as difficultyLevel, " +
                    "s.name as subjectName, u.username as createdByUsername " +
                    "FROM Questions q " +
                    "LEFT JOIN Topics t ON q.topicId = t.id " +
                    "LEFT JOIN Difficulties d ON q.difficultyId = d.id " +
                    "LEFT JOIN Subjects s ON q.subjectId = s.id " +
                    "LEFT JOIN Users u ON q.createdBy = u.id " +
                    "ORDER BY q.createdAt DESC";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Question question = mapResultSetToQuestion(rs);
                questions.add(question);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return questions;
    }

    // Lấy câu hỏi theo ID
    public Question getQuestionById(int questionId) {
        String sql = "SELECT q.*, t.name as topicName, d.level as difficultyLevel, " +
                    "s.name as subjectName, u.username as createdByUsername " +
                    "FROM Questions q " +
                    "LEFT JOIN Topics t ON q.topicId = t.id " +
                    "LEFT JOIN Difficulties d ON q.difficultyId = d.id " +
                    "LEFT JOIN Subjects s ON q.subjectId = s.id " +
                    "LEFT JOIN Users u ON q.createdBy = u.id " +
                    "WHERE q.id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, questionId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                Question question = mapResultSetToQuestion(rs);
                // Lấy đáp án đúng
                question.setCorrectAnswers(getCorrectAnswers(questionId));
                return question;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Thêm câu hỏi mới
    public boolean addQuestion(Question question, List<String> correctAnswers) {
        String sql = "INSERT INTO Questions (content, optionA, optionB, optionC, optionD, " +
                    "topicId, difficultyId, subjectId, createdBy) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        // Debug log
        System.out.println("DEBUG - QuestionDAO.addQuestion() - CreatedBy: " + question.getCreatedBy());
        
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, question.getContent());
            stmt.setString(2, question.getOptionA());
            stmt.setString(3, question.getOptionB());
            stmt.setString(4, question.getOptionC());
            stmt.setString(5, question.getOptionD());
            stmt.setInt(6, question.getTopicId());
            stmt.setInt(7, question.getDifficultyId());
            stmt.setInt(8, question.getSubjectId());
            stmt.setInt(9, question.getCreatedBy());
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int questionId = generatedKeys.getInt(1);
                    // Thêm đáp án đúng
                    return addCorrectAnswers(questionId, correctAnswers);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Cập nhật câu hỏi
    public boolean updateQuestion(Question question, List<String> correctAnswers) {
        String sql = "UPDATE Questions SET content = ?, optionA = ?, optionB = ?, optionC = ?, optionD = ?, " +
                    "topicId = ?, difficultyId = ?, subjectId = ? WHERE id = ?";
        
        // Debug log
        System.out.println("DEBUG - QuestionDAO.updateQuestion() - ID: " + question.getId());
        System.out.println("DEBUG - QuestionDAO.updateQuestion() - Difficulty ID: " + question.getDifficultyId());
        System.out.println("DEBUG - QuestionDAO.updateQuestion() - Topic ID: " + question.getTopicId());
        System.out.println("DEBUG - QuestionDAO.updateQuestion() - Subject ID: " + question.getSubjectId());
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, question.getContent());
            stmt.setString(2, question.getOptionA());
            stmt.setString(3, question.getOptionB());
            stmt.setString(4, question.getOptionC());
            stmt.setString(5, question.getOptionD());
            stmt.setInt(6, question.getTopicId());
            stmt.setInt(7, question.getDifficultyId());
            stmt.setInt(8, question.getSubjectId());
            stmt.setInt(9, question.getId());
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                // Xóa đáp án cũ và thêm đáp án mới
                deleteCorrectAnswers(question.getId());
                return addCorrectAnswers(question.getId(), correctAnswers);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Xóa câu hỏi
    public boolean deleteQuestion(int questionId) {
        // Xóa đáp án đúng trước
        deleteCorrectAnswers(questionId);
        
        String sql = "DELETE FROM Questions WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, questionId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Lấy đáp án đúng của câu hỏi
    private List<String> getCorrectAnswers(int questionId) {
        List<String> correctAnswers = new ArrayList<>();
        String sql = "SELECT optionLabel FROM Question_CorrectAnswers WHERE questionId = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, questionId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                correctAnswers.add(rs.getString("optionLabel"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return correctAnswers;
    }

    // Thêm đáp án đúng
    private boolean addCorrectAnswers(int questionId, List<String> correctAnswers) {
        String sql = "INSERT INTO Question_CorrectAnswers (questionId, optionLabel) VALUES (?, ?)";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            for (String answer : correctAnswers) {
                stmt.setInt(1, questionId);
                stmt.setString(2, answer);
                stmt.addBatch();
            }
            stmt.executeBatch();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Xóa đáp án đúng
    private void deleteCorrectAnswers(int questionId) {
        String sql = "DELETE FROM Question_CorrectAnswers WHERE questionId = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, questionId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Map ResultSet to Question object
    private Question mapResultSetToQuestion(ResultSet rs) throws SQLException {
        Question question = new Question();
        question.setId(rs.getInt("id"));
        question.setContent(rs.getString("content"));
        question.setOptionA(rs.getString("optionA"));
        question.setOptionB(rs.getString("optionB"));
        question.setOptionC(rs.getString("optionC"));
        question.setOptionD(rs.getString("optionD"));
        question.setTopicId(rs.getInt("topicId"));
        question.setDifficultyId(rs.getInt("difficultyId"));
        question.setSubjectId(rs.getInt("subjectId"));
        question.setCreatedBy(rs.getInt("createdBy"));
        question.setCreatedAt(rs.getTimestamp("createdAt").toLocalDateTime());
        
        // Set related objects
        if (rs.getString("topicName") != null) {
            Topic topic = new Topic();
            topic.setId(rs.getInt("topicId"));
            topic.setName(rs.getString("topicName"));
            question.setTopic(topic);
        }
        
        if (rs.getString("difficultyLevel") != null) {
            Difficulty difficulty = new Difficulty();
            difficulty.setId(rs.getInt("difficultyId"));
            difficulty.setLevel(rs.getString("difficultyLevel"));
            question.setDifficulty(difficulty);
        }
        
        if (rs.getString("subjectName") != null) {
            Subject subject = new Subject();
            subject.setId(rs.getInt("subjectId"));
            subject.setName(rs.getString("subjectName"));
            question.setSubject(subject);
        }
        
        if (rs.getString("createdByUsername") != null) {
            User user = new User();
            user.setId(rs.getInt("createdBy"));
            user.setUsername(rs.getString("createdByUsername"));
            question.setCreatedByUser(user);
        }
        
        return question;
    }
}
