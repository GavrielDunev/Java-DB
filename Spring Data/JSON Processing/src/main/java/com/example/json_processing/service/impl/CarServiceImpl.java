package com.example.json_processing.service.impl;

import com.example.json_processing.model.dto.CarSeedDto;
import com.example.json_processing.model.dto.CarsAndPartsInfoDto;
import com.example.json_processing.model.dto.CarsFromMakeInfoDto;
import com.example.json_processing.model.entity.Car;
import com.example.json_processing.repository.CarRepository;
import com.example.json_processing.service.CarService;
import com.example.json_processing.service.PartService;
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
public class CarServiceImpl implements CarService {
    private static final String CARS_FILE_NAME = "cars.json";

    private final CarRepository carRepository;
    private final Gson gson;
    private final ModelMapper modelMapper;
    private final ValidationUtil validationUtil;
    private final PartService partService;

    public CarServiceImpl(CarRepository carRepository, Gson gson, ModelMapper modelMapper, ValidationUtil validationUtil, PartService partService) {
        this.carRepository = carRepository;
        this.gson = gson;
        this.modelMapper = modelMapper;
        this.validationUtil = validationUtil;
        this.partService = partService;
    }

    @Override
    public void seedCars() throws IOException {
        if (this.carRepository.count() > 0) {
            return;
        }

        CarSeedDto[] carSeedDtos = gson.fromJson(Files.readString(Path.of(RESOURCES_FILE_PATH + CARS_FILE_NAME)),
                CarSeedDto[].class);

        Arrays.stream(carSeedDtos)
                .filter(validationUtil::isValid)
                .map(carSeedDto -> {
                    Car car = modelMapper.map(carSeedDto, Car.class);
                    car.setParts(partService.getRandomParts());
                    return car;
                })
                .forEach(this.carRepository::save);
    }

    @Override
    public Car getRandomCar() {
        long randomId = ThreadLocalRandom.current().nextLong(1, this.carRepository.count() + 1);
        return this.carRepository.findById(randomId).orElse(null);
    }

    @Override
    public List<CarsFromMakeInfoDto> findAllByMakeToyotaOrderByModel() {
        return this.carRepository.findAllByMakeOrderByModelAscTravelledDistanceDesc("Toyota")
                .stream()
                .map(car -> modelMapper.map(car, CarsFromMakeInfoDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<CarsAndPartsInfoDto> findAll() {
        return this.carRepository.findAll()
                .stream()
                .map(car -> modelMapper.map(car, CarsAndPartsInfoDto.class))
                .collect(Collectors.toList());
    }
}
