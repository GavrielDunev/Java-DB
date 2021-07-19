package com.example.json_processing.service.impl;

import com.example.json_processing.model.dto.*;
import com.example.json_processing.model.entity.User;
import com.example.json_processing.repository.UserRepository;
import com.example.json_processing.service.UserService;
import com.example.json_processing.util.ValidationUtil;
import com.google.gson.Gson;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import static com.example.json_processing.constant.GlobalConstants.RESOURCES_FILE_PATH;

@Service
public class UserServiceImpl implements UserService {
    private static final String USERS_FILE_NAME = "users.json";

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final ValidationUtil validationUtil;
    private final Gson gson;

    public UserServiceImpl(UserRepository userRepository, ModelMapper modelMapper, ValidationUtil validationUtil, Gson gson) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.validationUtil = validationUtil;
        this.gson = gson;
    }

    @Override
    public void seedUsers() throws IOException {
        if (this.userRepository.count() > 0) {
            return;
        }

        Arrays.stream(gson.fromJson(Files.readString(Path.of(RESOURCES_FILE_PATH + USERS_FILE_NAME)),
                UserSeedDto[].class))
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
    public List<UsersSoldProductsDto> findAllWithSoldProductsOrderByLastAndFirstName() {
        return this.userRepository.findAllWithSoldProducts()
                .stream()
                .map(user -> modelMapper.map(user, UsersSoldProductsDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public UsersAndUsersCountDto findAllWithSoldProductsOrderBySoldProductsCount() {
        List<User> users = this.userRepository.findAllBySoldProductsNotEmptyOrderBySoldProductsSizeDesc();
        UsersAndUsersCountDto usersAndProductsDto = new UsersAndUsersCountDto();
        usersAndProductsDto.setUsersCount(users.size());

        List<UserFirstLastNameAgeDto> collect = users.stream()
                .map(user -> {
                    UserFirstLastNameAgeDto mappedUser = modelMapper.map(user, UserFirstLastNameAgeDto.class);
                    mappedUser.getSoldProducts().setCount(user.getSoldProducts().size());
                    return mappedUser;
                })
                .collect(Collectors.toList());
        usersAndProductsDto.setUsers(collect);

        return usersAndProductsDto;
    }
}
