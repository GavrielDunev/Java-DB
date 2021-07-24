package com.example.xml_processing.service;


import com.example.xml_processing.model.dto.UserSeedDto;
import com.example.xml_processing.model.dto.UserSoldProductsRootDto;
import com.example.xml_processing.model.dto.UsersAndProductsRootDto;
import com.example.xml_processing.model.entity.User;

import java.io.IOException;
import java.util.List;

public interface UserService {
    void seedUsers(List<UserSeedDto> users) throws IOException;

    User getRandomUser();

    long getCountOfUsers();

    UserSoldProductsRootDto findAllWithSoldProductsAndBuyer();

    UsersAndProductsRootDto findAllWithMoreThanOneSoldProduct();
}
