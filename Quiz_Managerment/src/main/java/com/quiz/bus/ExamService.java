package com.quiz.bus;

import com.quiz.dao.ExamDAO;
import com.quiz.model.Exam;
import com.quiz.model.Question;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Business layer for Exam-related operations.
 */
public class ExamService {
    private final ExamDAO examDAO;

    public ExamService() {
        this.examDAO = new ExamDAO();
    }

    public List<Exam> getAllExams() {
        List<Exam> exams = examDAO.getAllExams();
        return exams != null ? exams : Collections.emptyList();
    }

    public Exam getExamById(int examId) {
        return examDAO.getExamById(examId);
    }

    public boolean createExam(Exam exam, List<Question> selectedQuestions) {
        List<Integer> questionIds = extractQuestionIds(selectedQuestions);
        return examDAO.addExam(exam, questionIds);
    }

    public boolean updateExam(Exam exam, List<Question> selectedQuestions) {
        List<Integer> questionIds = extractQuestionIds(selectedQuestions);
        return examDAO.updateExam(exam, questionIds);
    }

    public boolean deleteExam(int examId) {
        return examDAO.deleteExam(examId);
    }

    private List<Integer> extractQuestionIds(List<Question> questions) {
        if (questions == null || questions.isEmpty()) return Collections.emptyList();
        List<Integer> ids = new ArrayList<>(questions.size());
        for (Question q : questions) {
            ids.add(q.getId());
        }
        return ids;
    }
}


