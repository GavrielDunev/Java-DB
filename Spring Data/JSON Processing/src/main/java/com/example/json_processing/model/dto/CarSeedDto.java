package com.example.json_processing.model.dto;

import com.google.gson.annotations.Expose;

import javax.validation.constraints.Positive;

public class CarSeedDto {
    @Expose
    private String make;
    @Expose
    private String model;
    @Expose
    private Double travelledDistance;

    public CarSeedDto() {
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    @Positive
    public Double getTravelledDistance() {
        return travelledDistance;
    }

    public void setTravelledDistance(Double travelledDistance) {
        this.travelledDistance = travelledDistance;
    }
}