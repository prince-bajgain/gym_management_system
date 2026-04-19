package com.gym.user.model.dao;

import com.gym.user.model.User;

public interface UserInterface {
    boolean registerUser(User user);
    User loginUser(String email, String password);
}
