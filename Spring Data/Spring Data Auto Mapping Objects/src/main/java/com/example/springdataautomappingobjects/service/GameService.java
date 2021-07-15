package com.example.springdataautomappingobjects.service;

import com.example.springdataautomappingobjects.model.dto.GameAddDto;
import com.example.springdataautomappingobjects.model.dto.GameViewDto;

import java.util.List;

public interface GameService {

    void addGame(GameAddDto gameAddDto);

    void editGame(Long id, List<String[]> values);

    void deleteGame(Long id);

    void printAllGames();

    void printGameDetails(GameViewDto gameViewDto);

    void printOwnedGames();

}
