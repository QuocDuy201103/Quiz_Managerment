package com.quiz.bus;

import com.quiz.dao.ExamResultDAO;
import com.quiz.model.ExamAttemptStat;
import com.quiz.model.ExamResult;
import com.quiz.model.UserAnswer;

import java.util.Collections;
import java.util.List;

/**
 * Business layer for ExamResult-related operations.
 */
public class ExamResultService {
    private final ExamResultDAO examResultDAO;

    public ExamResultService() {
        this.examResultDAO = new ExamResultDAO();
    }

    public List<ExamResult> getAllResults() {
        List<ExamResult> results = examResultDAO.getAllExamResults();
        return results != null ? results : Collections.emptyList();
    }

    public List<ExamResult> getResultsByUserId(int userId) {
        List<ExamResult> results = examResultDAO.getExamResultsByUser(userId);
        return results != null ? results : Collections.emptyList();
    }

    public List<ExamResult> getResultsByExamId(int examId) {
        List<ExamResult> results = examResultDAO.getExamResultsByExam(examId);
        return results != null ? results : Collections.emptyList();
    }

    public boolean createExamResult(ExamResult examResult, List<UserAnswer> userAnswers) {
        return examResultDAO.addExamResult(examResult, userAnswers);
    }

    public List<ExamAttemptStat> getAttemptCountsPerExam() {
        List<ExamAttemptStat> stats = examResultDAO.getAttemptCountsPerExam();
        return stats != null ? stats : Collections.emptyList();
    }

    public List<ExamAttemptStat> getAttemptCountsPerExam(java.time.LocalDate startDate, java.time.LocalDate endDate) {
        List<ExamAttemptStat> stats = examResultDAO.getAttemptCountsPerExam(startDate, endDate);
        return stats != null ? stats : Collections.emptyList();
    }
}


