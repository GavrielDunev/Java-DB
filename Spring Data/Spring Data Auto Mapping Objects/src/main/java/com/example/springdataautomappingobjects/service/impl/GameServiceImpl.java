package com.example.springdataautomappingobjects.service.impl;

import com.example.springdataautomappingobjects.model.dto.GameAddDto;
import com.example.springdataautomappingobjects.model.dto.GameViewDto;
import com.example.springdataautomappingobjects.model.entity.Game;
import com.example.springdataautomappingobjects.repository.GameRepository;
import com.example.springdataautomappingobjects.service.GameService;
import com.example.springdataautomappingobjects.service.UserService;
import com.example.springdataautomappingobjects.util.ValidationUtil;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import javax.validation.ConstraintViolation;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;

@Service
public class GameServiceImpl implements GameService {
    private final GameRepository gameRepository;
    private final ModelMapper modelMapper;
    private final ValidationUtil validationUtil;
    private final UserService userService;

    public GameServiceImpl(GameRepository gameRepository, ModelMapper modelMapper, ValidationUtil validationUtil, UserService userService) {
        this.gameRepository = gameRepository;
        this.modelMapper = modelMapper;
        this.validationUtil = validationUtil;
        this.userService = userService;
    }

    @Override
    public void addGame(GameAddDto gameAddDto) {
        if (!this.userService.isUserLoggedIn()) {
            return;
        }

        if (!this.userService.isUserAdmin()) {
            return;
        }

        if (!checkIsDataValid(gameAddDto)) {
            return;
        }

        Game game = modelMapper.map(gameAddDto, Game.class);

        this.gameRepository.save(game);
        System.out.println("Added " + gameAddDto.getTitle());
    }


    @Override
    public void editGame(Long id, List<String[]> values) {
        if (!this.userService.isUserLoggedIn()) {
            return;
        }

        if (!this.userService.isUserAdmin()) {
            return;
        }

        Game gameToEdit = this.gameRepository.findById(id).orElse(null);
        if (gameToEdit == null) {
            System.out.println("Enter a valid game id.");
            return;
        }

        values.forEach(value -> {
            switch (value[0]) {
                case "title" -> gameToEdit.setTitle(value[1]);
                case "trailer" -> gameToEdit.setTrailer(value[1]);
                case "imageThumbnail" -> gameToEdit.setImageThumbnail(value[1]);
                case "size" -> gameToEdit.setSize(Double.parseDouble(value[1]));
                case "price" -> gameToEdit.setPrice(new BigDecimal(value[1]));
                case "description" -> gameToEdit.setDescription(value[1]);
                case "releaseDate" -> gameToEdit.setReleaseDate(LocalDate.parse(value[1],
                        DateTimeFormatter.ofPattern("dd-MM-yyyy")));
            }
        });

        this.gameRepository.save(gameToEdit);
        System.out.println("Edited " + gameToEdit.getTitle());
    }

    @Override
    public void deleteGame(Long id) {
        if (!this.userService.isUserLoggedIn()) {
            return;
        }

        if (!this.userService.isUserAdmin()) {
            return;
        }

        Game gameToDelete = this.gameRepository.findById(id).orElse(null);
        if (gameToDelete == null) {
            System.out.println("Enter a valid game id.");
            return;
        }

        this.gameRepository.delete(gameToDelete);
        System.out.println("Deleted " + gameToDelete.getTitle());
    }

    @Override
    public void printAllGames() {
        this.gameRepository.findAll()
                .stream()
                .map(game -> String.format("%s %.2f", game.getTitle(), game.getPrice()))
                .forEach(System.out::println);
    }

    @Override
    public void printGameDetails(GameViewDto gameViewDto) {
        if (!checkIsDataValid(gameViewDto)) {
            return;
        }

        Game game = this.gameRepository.findByTitle(gameViewDto.getTitle());
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Title: %s%n", game.getTitle()))
                .append(String.format("Price: %.2f%n", game.getPrice()))
                .append(String.format("Description: %s%n", game.getDescription()))
                .append(String.format("Release date: %s%n", game.getReleaseDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))));
        System.out.println(sb);
    }

    @Override
    public void printOwnedGames() {
        this.userService.getLoggedInUser().getGames()
                .stream().map(Game::getTitle)
                .forEach(System.out::println);
    }

    private <T> boolean checkIsDataValid(T dto) {
        Set<ConstraintViolation<T>> violation = validationUtil.getViolations(dto);
        if (!violation.isEmpty()) {
            violation.stream()
                    .map(ConstraintViolation::getMessage)
                    .forEach(System.out::println);
            return false;
        }
        return true;
    }
}
