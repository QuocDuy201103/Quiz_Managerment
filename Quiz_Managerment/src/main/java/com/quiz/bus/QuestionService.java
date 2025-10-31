package com.quiz.bus;

import com.quiz.dao.QuestionDAO;
import com.quiz.model.Question;

import java.util.Collections;
import java.util.List;

public class QuestionService {
    private final QuestionDAO questionDAO;

    public QuestionService() {
        this.questionDAO = new QuestionDAO();
    }

    public List<Question> getAllQuestions() {
        List<Question> questions = questionDAO.getAllQuestions();
        return questions != null ? questions : Collections.emptyList();
    }
}


