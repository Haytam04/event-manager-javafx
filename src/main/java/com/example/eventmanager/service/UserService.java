package com.example.eventmanager.service;

import com.example.eventmanager.dao.UserDAO;
import com.example.eventmanager.entity.User;

public class UserService {
    private UserDAO userDAO = new UserDAO();

    public boolean register(String username, String password) {
        if (userDAO.findByUsername(username) != null) return false;
        User user = new User();
        user.setUsername(username);
        user.setPassword(password); // In production, use BCrypt to hash this!
        userDAO.save(user);
        return true;
    }

    public User login(String username, String password) {
        User user = userDAO.findByUsername(username);
        if (user != null && user.getPassword().equals(password)) {
            return user;
        }
        return null;
    }
}