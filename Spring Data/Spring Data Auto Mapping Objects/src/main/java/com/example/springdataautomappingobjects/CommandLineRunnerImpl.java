package com.example.springdataautomappingobjects;

import com.example.springdataautomappingobjects.model.dto.GameAddDto;
import com.example.springdataautomappingobjects.model.dto.GameViewDto;
import com.example.springdataautomappingobjects.model.dto.UserLoginDto;
import com.example.springdataautomappingobjects.model.dto.UserRegisterDto;
import com.example.springdataautomappingobjects.service.GameService;
import com.example.springdataautomappingobjects.service.OrderService;
import com.example.springdataautomappingobjects.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.stream.Collectors;

@Component
public class CommandLineRunnerImpl implements CommandLineRunner {
    private final BufferedReader bufferedReader;
    private final UserService userService;
    private final GameService gameService;
    private final OrderService orderService;

    public CommandLineRunnerImpl(UserService userService, GameService gameService, OrderService orderService) {
        this.userService = userService;
        this.gameService = gameService;
        this.orderService = orderService;
        this.bufferedReader = new BufferedReader(new InputStreamReader(System.in));
    }

    @Override
    public void run(String... args) throws Exception {
        while (true) {
            System.out.println("Enter commands:");
            String[] tokens = bufferedReader.readLine().split("\\|");
            switch (tokens[0]) {
                case "RegisterUser" -> this.userService.registerUser(new UserRegisterDto(tokens[1], tokens[2],
                        tokens[3], tokens[4]));
                case "LoginUser" -> this.userService.loginUser(new UserLoginDto(tokens[1], tokens[2]));
                case "Logout" -> this.userService.logoutUser();
                case "AddGame" -> this.gameService.addGame(new GameAddDto(tokens[1], new BigDecimal(tokens[2]),
                        Double.parseDouble(tokens[3]), tokens[4], tokens[5], tokens[6],
                        LocalDate.parse(tokens[7], DateTimeFormatter.ofPattern("dd-MM-yyyy"))));
                case "EditGame" -> this.gameService.editGame(Long.parseLong(tokens[1]),
                        Arrays.stream(tokens).skip(2)
                                .map(t -> t.split("="))
                                .collect(Collectors.toList()));
                case "DeleteGame" -> this.gameService.deleteGame(Long.parseLong(tokens[1]));
                case "AllGames" -> this.gameService.printAllGames();
                case "DetailGame" -> this.gameService.printGameDetails(new GameViewDto(tokens[1]));
                case "OwnedGames" -> this.gameService.printOwnedGames();
                case "AddItem" -> this.orderService.addItem(new GameViewDto(tokens[1]));
                case "RemoveItem" -> this.orderService.removeItem(new GameViewDto(tokens[1]));
                case "BuyItem" -> this.userService.buyItems();
            }
        }
    }
}
