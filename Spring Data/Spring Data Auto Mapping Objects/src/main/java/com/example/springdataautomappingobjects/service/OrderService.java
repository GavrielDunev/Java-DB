package com.example.springdataautomappingobjects.service;

import com.example.springdataautomappingobjects.model.dto.GameViewDto;

public interface OrderService {
    void addItem(GameViewDto gameViewDto);

    void removeItem(GameViewDto gameViewDto);
}
