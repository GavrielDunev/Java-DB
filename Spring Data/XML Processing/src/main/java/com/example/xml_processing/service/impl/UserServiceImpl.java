package com.example.xml_processing.service.impl;

import com.example.xml_processing.model.dto.*;
import com.example.xml_processing.model.entity.User;
import com.example.xml_processing.repository.UserRepository;
import com.example.xml_processing.service.UserService;
import com.example.xml_processing.util.ValidationUtil;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final ValidationUtil validationUtil;
    private final ModelMapper modelMapper;

    public UserServiceImpl(UserRepository userRepository, ValidationUtil validationUtil, ModelMapper modelMapper) {
        this.userRepository = userRepository;
        this.validationUtil = validationUtil;
        this.modelMapper = modelMapper;
    }

    @Override
    public void seedUsers(List<UserSeedDto> users) throws IOException {
        users.stream()
                .filter(validationUtil::isValid)
                .map(userSeedDto -> modelMapper.map(userSeedDto, User.class))
                .forEach(this.userRepository::save);
    }

    @Override
    public User getRandomUser() {
        long randomId = ThreadLocalRandom.current().nextLong(1, this.userRepository.count() + 1);
        return this.userRepository.findById(randomId).orElse(null);
    }

    @Override
    public long getCountOfUsers() {
        return this.userRepository.count();
    }

    @Override
    public UserSoldProductsRootDto findAllWithSoldProductsAndBuyer() {
        List<User> users = this.userRepository.findAllWithMoreThanOneSoldProduct();

        List<UserSoldProductsDto> collect = users.stream()
                .map(user -> modelMapper.map(user, UserSoldProductsDto.class))
                .collect(Collectors.toList());

        UserSoldProductsRootDto userRootDto = new UserSoldProductsRootDto();
        userRootDto.setUsers(collect);
        return userRootDto;
    }

    @Override
    public UsersAndProductsRootDto findAllWithMoreThanOneSoldProduct() {
        List<User> users = this.userRepository.findAllBySoldProductsNotEmptyOrderBySoldProductsSizeDesc();
        UsersAndProductsRootDto rootDto = new UsersAndProductsRootDto();
        rootDto.setCount(users.size());

        List<UsersAndProductsDto> collect = users.stream()
                .map(user -> modelMapper.map(user, UsersAndProductsDto.class))
                .collect(Collectors.toList());
        rootDto.setUsers(collect);

        return rootDto;
    }
}
