package com.quiz.dao;

import com.quiz.database.DatabaseConnection;
import com.quiz.model.Exam;
import com.quiz.model.Question;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object cho Exam
 */
public class ExamDAO {
    private Connection connection;

    public ExamDAO() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }

    // Lấy tất cả đề thi
    public List<Exam> getAllExams() {
        List<Exam> exams = new ArrayList<>();
        String sql = "SELECT e.*, s.name as subjectName, u.username as createdByUsername, " +
                    "COUNT(eq.questionId) as questionCount " +
                    "FROM Exams e " +
                    "LEFT JOIN Subjects s ON e.subjectId = s.id " +
                    "LEFT JOIN Users u ON e.createdBy = u.id " +
                    "LEFT JOIN Exam_Questions eq ON e.id = eq.examId " +
                    "GROUP BY e.id, e.title, e.duration, e.subjectId, e.createdBy, e.createdAt, s.name, u.username " +
                    "ORDER BY e.createdAt DESC";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Exam exam = mapResultSetToExam(rs);
                // Set question count from the query result
                int questionCount = rs.getInt("questionCount");
                exam.setQuestionCount(questionCount);
                exams.add(exam);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return exams;
    }

    // Lấy đề thi theo ID
    public Exam getExamById(int examId) {
        String sql = "SELECT e.*, s.name as subjectName, u.username as createdByUsername " +
                    "FROM Exams e " +
                    "LEFT JOIN Subjects s ON e.subjectId = s.id " +
                    "LEFT JOIN Users u ON e.createdBy = u.id " +
                    "WHERE e.id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, examId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                Exam exam = mapResultSetToExam(rs);
                // Lấy danh sách câu hỏi
                exam.setQuestions(getExamQuestions(examId));
                return exam;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Thêm đề thi mới
    public boolean addExam(Exam exam, List<Integer> questionIds) {
        String sql = "INSERT INTO Exams (title, duration, subjectId, createdBy) VALUES (?, ?, ?, ?)";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, exam.getTitle());
            stmt.setInt(2, exam.getDuration());
            stmt.setInt(3, exam.getSubjectId());
            stmt.setInt(4, exam.getCreatedBy());
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int examId = generatedKeys.getInt(1);
                    // Thêm câu hỏi vào đề thi
                    return addExamQuestions(examId, questionIds);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Cập nhật đề thi
    public boolean updateExam(Exam exam, List<Integer> questionIds) {
        String sql = "UPDATE Exams SET title = ?, duration = ?, subjectId = ? WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, exam.getTitle());
            stmt.setInt(2, exam.getDuration());
            stmt.setInt(3, exam.getSubjectId());
            stmt.setInt(4, exam.getId());
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                // Xóa câu hỏi cũ và thêm câu hỏi mới
                deleteExamQuestions(exam.getId());
                return addExamQuestions(exam.getId(), questionIds);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Xóa đề thi
    public boolean deleteExam(int examId) {
        try {
            // Bắt đầu transaction để đảm bảo tính toàn vẹn dữ liệu
            connection.setAutoCommit(false);
            
            // 1. Xóa UserAnswers liên quan đến ExamResults của đề thi này
            deleteUserAnswersByExamId(examId);
            
            // 2. Xóa ExamResults liên quan đến đề thi này
            deleteExamResultsByExamId(examId);
            
            // 3. Xóa câu hỏi trong đề thi
            deleteExamQuestions(examId);
            
            // 4. Cuối cùng xóa đề thi
            String sql = "DELETE FROM Exams WHERE id = ?";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setInt(1, examId);
                int affectedRows = stmt.executeUpdate();
                
                if (affectedRows > 0) {
                    connection.commit();
                    return true;
                } else {
                    connection.rollback();
                    return false;
                }
            }
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
            e.printStackTrace();
            return false;
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // Lấy câu hỏi trong đề thi
    private List<Question> getExamQuestions(int examId) {
        List<Question> questions = new ArrayList<>();
        String sql = "SELECT q.*, t.name as topicName, d.level as difficultyLevel, " +
                    "s.name as subjectName " +
                    "FROM Exam_Questions eq " +
                    "JOIN Questions q ON eq.questionId = q.id " +
                    "LEFT JOIN Topics t ON q.topicId = t.id " +
                    "LEFT JOIN Difficulties d ON q.difficultyId = d.id " +
                    "LEFT JOIN Subjects s ON q.subjectId = s.id " +
                    "WHERE eq.examId = ? " +
                    "ORDER BY q.id";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, examId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
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
                    com.quiz.model.Topic topic = new com.quiz.model.Topic();
                    topic.setId(rs.getInt("topicId"));
                    topic.setName(rs.getString("topicName"));
                    question.setTopic(topic);
                }
                
                if (rs.getString("difficultyLevel") != null) {
                    com.quiz.model.Difficulty difficulty = new com.quiz.model.Difficulty();
                    difficulty.setId(rs.getInt("difficultyId"));
                    difficulty.setLevel(rs.getString("difficultyLevel"));
                    question.setDifficulty(difficulty);
                }
                
                if (rs.getString("subjectName") != null) {
                    com.quiz.model.Subject subject = new com.quiz.model.Subject();
                    subject.setId(rs.getInt("subjectId"));
                    subject.setName(rs.getString("subjectName"));
                    question.setSubject(subject);
                }
                
                questions.add(question);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return questions;
    }

    // Thêm câu hỏi vào đề thi
    private boolean addExamQuestions(int examId, List<Integer> questionIds) {
        String sql = "INSERT INTO Exam_Questions (examId, questionId) VALUES (?, ?)";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            for (Integer questionId : questionIds) {
                stmt.setInt(1, examId);
                stmt.setInt(2, questionId);
                stmt.addBatch();
            }
            stmt.executeBatch();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Xóa câu hỏi khỏi đề thi
    private void deleteExamQuestions(int examId) {
        String sql = "DELETE FROM Exam_Questions WHERE examId = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, examId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    // Xóa UserAnswers liên quan đến ExamResults của đề thi
    private void deleteUserAnswersByExamId(int examId) {
        String sql = "DELETE ua FROM UserAnswers ua " +
                    "INNER JOIN ExamResults er ON ua.resultId = er.id " +
                    "WHERE er.examId = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, examId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    // Xóa ExamResults liên quan đến đề thi
    private void deleteExamResultsByExamId(int examId) {
        String sql = "DELETE FROM ExamResults WHERE examId = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, examId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Map ResultSet to Exam object
    private Exam mapResultSetToExam(ResultSet rs) throws SQLException {
        Exam exam = new Exam();
        exam.setId(rs.getInt("id"));
        exam.setTitle(rs.getString("title"));
        exam.setDuration(rs.getInt("duration"));
        exam.setSubjectId(rs.getInt("subjectId"));
        exam.setCreatedBy(rs.getInt("createdBy"));
        exam.setCreatedAt(rs.getTimestamp("createdAt").toLocalDateTime());
        
        // Set related objects
        if (rs.getString("subjectName") != null) {
            com.quiz.model.Subject subject = new com.quiz.model.Subject();
            subject.setId(rs.getInt("subjectId"));
            subject.setName(rs.getString("subjectName"));
            exam.setSubject(subject);
        }
        
        if (rs.getString("createdByUsername") != null) {
            com.quiz.model.User user = new com.quiz.model.User();
            user.setId(rs.getInt("createdBy"));
            user.setUsername(rs.getString("createdByUsername"));
            exam.setCreatedByUser(user);
        }
        
        return exam;
    }
}
