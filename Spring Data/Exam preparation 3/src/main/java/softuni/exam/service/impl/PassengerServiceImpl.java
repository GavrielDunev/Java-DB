package softuni.exam.service.impl;

import com.google.gson.Gson;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import softuni.exam.models.dto.PassengerSeedDto;
import softuni.exam.models.entity.Passenger;
import softuni.exam.repository.PassengerRepository;
import softuni.exam.service.PassengerService;
import softuni.exam.service.TownService;
import softuni.exam.util.ValidationUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

@Service
public class PassengerServiceImpl implements PassengerService {

    private static final String PASSENGERS_FILE_PATH = "src/main/resources/files/json/passengers.json";

    private final PassengerRepository passengerRepository;
    private final ModelMapper modelMapper;
    private final Gson gson;
    private final ValidationUtil validationUtil;
    private final TownService townService;

    public PassengerServiceImpl(PassengerRepository passengerRepository, ModelMapper modelMapper, Gson gson, ValidationUtil validationUtil, TownService townService) {
        this.passengerRepository = passengerRepository;
        this.modelMapper = modelMapper;
        this.gson = gson;
        this.validationUtil = validationUtil;
        this.townService = townService;
    }

    @Override
    public boolean areImported() {
        return this.passengerRepository.count() > 0;
    }

    @Override
    public String readPassengersFileContent() throws IOException {
        return Files.readString(Path.of(PASSENGERS_FILE_PATH));
    }

    @Override
    public String importPassengers() throws IOException {
        PassengerSeedDto[] passengerSeedDtos = gson.fromJson(readPassengersFileContent(), PassengerSeedDto[].class);
        StringBuilder sb = new StringBuilder();

        Arrays.stream(passengerSeedDtos)
                .filter(passengerSeedDto -> {
                    boolean isValid = this.validationUtil.isValid(passengerSeedDto);

                    sb.append(isValid ? String.format("Successfully imported Passenger %s - %s",
                            passengerSeedDto.getLastName(), passengerSeedDto.getEmail())
                            : "Invalid Passenger")
                            .append(System.lineSeparator());

                    return isValid;
                })
                .map(passengerSeedDto -> {
                    Passenger passenger = modelMapper.map(passengerSeedDto, Passenger.class);
                    passenger.setTown(this.townService.findByName(passengerSeedDto.getTown()));
                    return passenger;
                })
                .forEach(this.passengerRepository::save);

        return sb.toString();
    }

    @Override
    public String getPassengersOrderByTicketsCountDescendingThenByEmail() {
        List<Passenger> passengers = this.passengerRepository.findAllOrderByCountOfTicketsDescThenByEmail();
        StringBuilder sb = new StringBuilder();

        passengers.forEach(passenger -> sb.append(String.format("Passenger %s  %s%n" +
                "\tEmail - %s%n" +
                "\tPhone - %s%n" +
                "\tNumber of tickets - %d%n", passenger.getFirstName(), passenger.getLastName(),
                passenger.getEmail(), passenger.getPhoneNumber(),
                passenger.getTickets().size())));
        return sb.toString();
    }

    @Override
    public Passenger findByEmail(String email) {
        return this.passengerRepository.findByEmail(email).orElse(null);
    }
}
