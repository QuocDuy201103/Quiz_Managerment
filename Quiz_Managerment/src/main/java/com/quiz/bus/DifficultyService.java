package com.quiz.bus;

import com.quiz.dao.DifficultyDAO;
import com.quiz.model.Difficulty;

import java.util.Collections;
import java.util.List;

/**
 * Business layer for Difficulty-related operations.
 */
public class DifficultyService {
    private final DifficultyDAO difficultyDAO;

    public DifficultyService() {
        this.difficultyDAO = new DifficultyDAO();
    }

    public List<Difficulty> getAllDifficulties() {
        List<Difficulty> difficulties = difficultyDAO.getAllDifficulties();
        return difficulties != null ? difficulties : Collections.emptyList();
    }

    public Difficulty getDifficultyById(int id) {
        return difficultyDAO.getDifficultyById(id);
    }

    public boolean createDifficulty(Difficulty difficulty) {
        return difficultyDAO.addDifficulty(difficulty);
    }

    public boolean updateDifficulty(Difficulty difficulty) {
        return difficultyDAO.updateDifficulty(difficulty);
    }

    public boolean deleteDifficulty(int id) {
        return difficultyDAO.deleteDifficulty(id);
    }
}

