package com.quiz.bus;

import com.quiz.dao.SubjectDAO;
import com.quiz.model.Subject;

import java.util.Collections;
import java.util.List;

/**
 * Business layer for Subject-related operations.
 */
public class SubjectService {
    private final SubjectDAO subjectDAO;

    public SubjectService() {
        this.subjectDAO = new SubjectDAO();
    }

    public List<Subject> getAllSubjects() {
        List<Subject> subjects = subjectDAO.getAllSubjects();
        return subjects != null ? subjects : Collections.emptyList();
    }

    public Subject getSubjectById(int subjectId) {
        return subjectDAO.getSubjectById(subjectId);
    }

    public boolean createSubject(Subject subject) {
        return subjectDAO.addSubject(subject);
    }

    public boolean updateSubject(Subject subject) {
        return subjectDAO.updateSubject(subject);
    }

    public boolean deleteSubject(int subjectId) {
        return subjectDAO.deleteSubject(subjectId);
    }
}


