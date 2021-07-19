package com.example.json_processing.model.dto;

import com.google.gson.annotations.Expose;

import java.util.List;

public class UsersAndUsersCountDto {
    @Expose
    private Integer usersCount;
    @Expose
    private List<UserFirstLastNameAgeDto> users;

    public UsersAndUsersCountDto() {
    }

    public int getUsersCount() {
        return usersCount;
    }

    public void setUsersCount(int usersCount) {
        this.usersCount = usersCount;
    }

    public List<UserFirstLastNameAgeDto> getUsers() {
        return users;
    }

    public void setUsers(List<UserFirstLastNameAgeDto> users) {
        this.users = users;
    }
}
