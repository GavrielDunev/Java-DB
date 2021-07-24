package com.example.xml_processing.service;


import com.example.xml_processing.model.entity.Car;

import java.io.IOException;


public interface CarService {
    void seedCars() throws IOException;

    Car getRandomCar();

}
