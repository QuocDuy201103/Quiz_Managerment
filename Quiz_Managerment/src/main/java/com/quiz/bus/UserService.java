package com.quiz.bus;

import com.quiz.dao.UserDAO;
import com.quiz.model.User;

public class UserService {
    private final UserDAO userDAO;

    public UserService() {
        this.userDAO = new UserDAO();
    }

    public User getById(int id) {
        return userDAO.getUserById(id);
    }
}


