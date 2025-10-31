package com.quiz.bus;

import com.quiz.dao.SubjectDAO;
import com.quiz.model.Subject;

import java.util.Collections;
import java.util.List;

public class SubjectService {
    private final SubjectDAO subjectDAO;

    public SubjectService() {
        this.subjectDAO = new SubjectDAO();
    }

    public List<Subject> getAllSubjects() {
        List<Subject> subjects = subjectDAO.getAllSubjects();
        return subjects != null ? subjects : Collections.emptyList();
    }
}


