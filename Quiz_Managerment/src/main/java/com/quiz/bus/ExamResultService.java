package com.quiz.bus;

import com.quiz.dao.ExamResultDAO;
import com.quiz.model.ExamResult;

import java.util.Collections;
import java.util.List;

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
}


