package com.quiz.bus;

import com.quiz.dao.UserDAO;
import com.quiz.model.Role;
import com.quiz.model.User;

import java.util.Collections;
import java.util.List;

/**
 * Business layer for User-related operations.
 */
public class UserService {
    private final UserDAO userDAO;

    public UserService() {
        this.userDAO = new UserDAO();
    }

    public User getById(int id) {
        return userDAO.getUserById(id);
    }

    public User login(String username, String password) {
        return userDAO.login(username, password);
    }

    public List<User> getAllUsers() {
        List<User> users = userDAO.getAllUsers();
        return users != null ? users : Collections.emptyList();
    }

    public boolean isUsernameExists(String username) {
        return userDAO.isUsernameExists(username);
    }

    public boolean isEmailExists(String email) {
        return userDAO.isEmailExists(email);
    }

    public boolean createUser(User user) {
        return userDAO.addUser(user);
    }

    public boolean updateUser(User user) {
        return userDAO.updateUser(user);
    }

    public boolean deleteUser(int userId) {
        return userDAO.deleteUser(userId);
    }

    public List<Role> getAllRoles() {
        List<Role> roles = userDAO.getAllRoles();
        return roles != null ? roles : Collections.emptyList();
    }
}


