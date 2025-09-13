package com.quiz.dao;

import com.quiz.database.DatabaseConnection;
import com.quiz.model.ExamResult;
import com.quiz.model.UserAnswer;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object cho ExamResult
 */
public class ExamResultDAO {
    private Connection connection;

    public ExamResultDAO() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }

    // Thêm kết quả thi
    public boolean addExamResult(ExamResult examResult, List<UserAnswer> userAnswers) {
        String sql = "INSERT INTO ExamResults (userId, examId, score, startTime, endTime) VALUES (?, ?, ?, ?, ?)";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, examResult.getUserId());
            stmt.setInt(2, examResult.getExamId());
            stmt.setDouble(3, examResult.getScore());
            stmt.setTimestamp(4, Timestamp.valueOf(examResult.getStartTime()));
            stmt.setTimestamp(5, Timestamp.valueOf(examResult.getEndTime()));
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int resultId = generatedKeys.getInt(1);
                    // Thêm câu trả lời của user
                    return addUserAnswers(resultId, userAnswers);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Thêm câu trả lời của user
    private boolean addUserAnswers(int resultId, List<UserAnswer> userAnswers) {
        String sql = "INSERT INTO UserAnswers (resultId, questionId, selectedOptions) VALUES (?, ?, ?)";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            for (UserAnswer userAnswer : userAnswers) {
                stmt.setInt(1, resultId);
                stmt.setInt(2, userAnswer.getQuestionId());
                stmt.setString(3, userAnswer.getSelectedOptions());
                stmt.addBatch();
            }
            stmt.executeBatch();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Lấy tất cả kết quả thi
    public List<ExamResult> getAllExamResults() {
        List<ExamResult> results = new ArrayList<>();
        String sql = "SELECT er.*, u.username, e.title as examTitle " +
                    "FROM ExamResults er " +
                    "LEFT JOIN Users u ON er.userId = u.id " +
                    "LEFT JOIN Exams e ON er.examId = e.id " +
                    "ORDER BY er.submittedAt DESC";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                ExamResult result = mapResultSetToExamResult(rs);
                results.add(result);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return results;
    }

    // Lấy kết quả thi theo user
    public List<ExamResult> getExamResultsByUser(int userId) {
        List<ExamResult> results = new ArrayList<>();
        String sql = "SELECT er.*, u.username, e.title as examTitle " +
                    "FROM ExamResults er " +
                    "LEFT JOIN Users u ON er.userId = u.id " +
                    "LEFT JOIN Exams e ON er.examId = e.id " +
                    "WHERE er.userId = ? " +
                    "ORDER BY er.submittedAt DESC";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                ExamResult result = mapResultSetToExamResult(rs);
                results.add(result);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return results;
    }

    // Lấy kết quả thi theo đề thi
    public List<ExamResult> getExamResultsByExam(int examId) {
        List<ExamResult> results = new ArrayList<>();
        String sql = "SELECT er.*, u.username, e.title as examTitle " +
                    "FROM ExamResults er " +
                    "LEFT JOIN Users u ON er.userId = u.id " +
                    "LEFT JOIN Exams e ON er.examId = e.id " +
                    "WHERE er.examId = ? " +
                    "ORDER BY er.submittedAt DESC";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, examId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                ExamResult result = mapResultSetToExamResult(rs);
                results.add(result);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return results;
    }

    // Map ResultSet to ExamResult object
    private ExamResult mapResultSetToExamResult(ResultSet rs) throws SQLException {
        ExamResult result = new ExamResult();
        result.setId(rs.getInt("id"));
        result.setUserId(rs.getInt("userId"));
        result.setExamId(rs.getInt("examId"));
        result.setScore(rs.getDouble("score"));
        result.setStartTime(rs.getTimestamp("startTime").toLocalDateTime());
        result.setEndTime(rs.getTimestamp("endTime").toLocalDateTime());
        result.setSubmittedAt(rs.getTimestamp("submittedAt").toLocalDateTime());
        
        // Set related objects
        if (rs.getString("username") != null) {
            com.quiz.model.User user = new com.quiz.model.User();
            user.setId(rs.getInt("userId"));
            user.setUsername(rs.getString("username"));
            result.setUser(user);
        }
        
        if (rs.getString("examTitle") != null) {
            com.quiz.model.Exam exam = new com.quiz.model.Exam();
            exam.setId(rs.getInt("examId"));
            exam.setTitle(rs.getString("examTitle"));
            result.setExam(exam);
        }
        
        return result;
    }
}
