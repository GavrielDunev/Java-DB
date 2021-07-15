package com.example.springdataautomappingobjects.service;

import com.example.springdataautomappingobjects.model.dto.GameViewDto;
import com.example.springdataautomappingobjects.model.dto.UserLoginDto;
import com.example.springdataautomappingobjects.model.dto.UserRegisterDto;
import com.example.springdataautomappingobjects.model.entity.User;

public interface UserService {
    void registerUser(UserRegisterDto userRegisterDto);

    void loginUser(UserLoginDto userLoginDto);

    void logoutUser();

    boolean isUserLoggedIn();

    boolean isUserAdmin();

    User getLoggedInUser();

    void buyItems();
}
