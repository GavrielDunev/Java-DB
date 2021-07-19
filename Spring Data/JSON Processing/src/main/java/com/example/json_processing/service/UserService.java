package com.example.json_processing.service;

import com.example.json_processing.model.dto.UsersAndUsersCountDto;
import com.example.json_processing.model.dto.UsersSoldProductsDto;
import com.example.json_processing.model.entity.User;

import java.io.IOException;
import java.util.List;

public interface UserService {
    void seedUsers() throws IOException;
    User getRandomUser();

    List<UsersSoldProductsDto> findAllWithSoldProductsOrderByLastAndFirstName();

    UsersAndUsersCountDto findAllWithSoldProductsOrderBySoldProductsCount();
}
