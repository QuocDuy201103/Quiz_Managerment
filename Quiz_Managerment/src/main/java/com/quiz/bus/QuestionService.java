package com.quiz.bus;

import com.quiz.dao.QuestionDAO;
import com.quiz.model.Question;

import java.util.Collections;
import java.util.List;

/**
 * Business layer for Question-related operations.
 */
public class QuestionService {
    private final QuestionDAO questionDAO;

    public QuestionService() {
        this.questionDAO = new QuestionDAO();
    }

    public List<Question> getAllQuestions() {
        List<Question> questions = questionDAO.getAllQuestions();
        return questions != null ? questions : Collections.emptyList();
    }

    public Question getQuestionById(int questionId) {
        return questionDAO.getQuestionById(questionId);
    }

    public boolean createQuestion(Question question, List<String> correctAnswers) {
        return questionDAO.addQuestion(question, correctAnswers);
    }

    public boolean updateQuestion(Question question, List<String> correctAnswers) {
        return questionDAO.updateQuestion(question, correctAnswers);
    }

    public boolean deleteQuestion(int questionId) {
        return questionDAO.deleteQuestion(questionId);
    }

    public boolean isQuestionUsedInExams(int questionId) {
        return questionDAO.isQuestionUsedInExams(questionId);
    }

    public List<String> getExamsUsingQuestion(int questionId) {
        List<String> exams = questionDAO.getExamsUsingQuestion(questionId);
        return exams != null ? exams : Collections.emptyList();
    }
}


