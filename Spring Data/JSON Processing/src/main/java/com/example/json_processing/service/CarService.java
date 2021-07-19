package com.example.json_processing.service;

import com.example.json_processing.model.dto.CarsAndPartsInfoDto;
import com.example.json_processing.model.dto.CarsFromMakeInfoDto;
import com.example.json_processing.model.entity.Car;

import java.io.IOException;
import java.util.List;

public interface CarService {
    void seedCars() throws IOException;

    Car getRandomCar();

    List<CarsFromMakeInfoDto> findAllByMakeToyotaOrderByModel();

    List<CarsAndPartsInfoDto> findAll();
}
