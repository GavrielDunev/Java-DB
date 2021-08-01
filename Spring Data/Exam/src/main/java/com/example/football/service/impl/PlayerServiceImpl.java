package com.example.football.service.impl;

import com.example.football.models.dto.PlayerSeedRootDto;
import com.example.football.models.entity.Player;
import com.example.football.repository.PlayerRepository;
import com.example.football.service.PlayerService;
import com.example.football.service.StatService;
import com.example.football.service.TeamService;
import com.example.football.service.TownService;
import com.example.football.util.ValidationUtil;
import com.example.football.util.XmlParser;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class PlayerServiceImpl implements PlayerService {

    private static final String PLAYERS_FILE_PATH = "src/main/resources/files/xml/players.xml";

    private final PlayerRepository playerRepository;
    private final ModelMapper modelMapper;
    private final ValidationUtil validationUtil;
    private final XmlParser xmlParser;
    private final StatService statService;
    private final TeamService teamService;
    private final TownService townService;

    public PlayerServiceImpl(PlayerRepository playerRepository, ModelMapper modelMapper, ValidationUtil validationUtil, XmlParser xmlParser, StatService statService, TeamService teamService, TownService townService) {
        this.playerRepository = playerRepository;
        this.modelMapper = modelMapper;
        this.validationUtil = validationUtil;
        this.xmlParser = xmlParser;
        this.statService = statService;
        this.teamService = teamService;
        this.townService = townService;
    }

    @Override
    public boolean areImported() {
        return this.playerRepository.count() > 0;
    }

    @Override
    public String readPlayersFileContent() throws IOException {
        return Files.readString(Path.of(PLAYERS_FILE_PATH));
    }

    @Override
    public String importPlayers() throws JAXBException {
        PlayerSeedRootDto playerSeedRootDto = xmlParser.fromFile(PLAYERS_FILE_PATH, PlayerSeedRootDto.class);
        StringBuilder sb = new StringBuilder();

        playerSeedRootDto.getPlayers()
                .stream()
                .filter(playerSeedDto -> {
                    boolean isValid = this.validationUtil.isValid(playerSeedDto)
                            && !isPlayerExisting(playerSeedDto.getEmail());

                    sb.append(isValid ? String.format("Successfully imported Player %s %s - %s",
                            playerSeedDto.getFirstName(), playerSeedDto.getLastName(), playerSeedDto.getPosition().name())
                            : "Invalid Player")
                            .append(System.lineSeparator());

                    return isValid;
                })
                .map(playerSeedDto -> {
                    Player player = modelMapper.map(playerSeedDto, Player.class);

                    player.setStat(this.statService.findById(playerSeedDto.getStat().getId()));
                    player.setTeam(this.teamService.findByName(playerSeedDto.getTeam().getName()));
                    player.setTown(this.townService.findByName(playerSeedDto.getTown().getName()));

                    return player;
                })
                .forEach(this.playerRepository::save);

        return sb.toString();
    }

    @Override
    public boolean isPlayerExisting(String email) {
        return this.playerRepository.existsByEmail(email);
    }

    @Override
    public String exportBestPlayers() {
        StringBuilder sb = new StringBuilder();

        List<Player> players = this.playerRepository.findAllByBirthDateAfterAndBirthDateBeforeOrderByShootingPassingEnduranceLastName(LocalDate.parse("01-01-1995", DateTimeFormatter.ofPattern("dd-MM-yyyy")),
                LocalDate.parse("01-01-2003", DateTimeFormatter.ofPattern("dd-MM-yyyy")));

        players.forEach(player -> sb.append(String.format("Player - %s %s\n" +
                "\tPosition - %s\n" +
                "\tTeam - %s\n" +
                "\tStadium - %s\n", player.getFirstName(), player.getLastName(),
                player.getPosition().name(), player.getTeam().getName(),
                player.getTeam().getStadiumName())));


        return sb.toString();
    }
}
