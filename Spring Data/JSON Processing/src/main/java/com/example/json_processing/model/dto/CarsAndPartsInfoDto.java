package com.example.json_processing.model.dto;

import com.google.gson.annotations.Expose;

import java.util.Set;

public class CarsAndPartsInfoDto {
    @Expose
    private String make;
    @Expose
    private String model;
    @Expose
    private Long travelledDistance;
    @Expose
    private Set<PartNamePriceDto> parts;

    public CarsAndPartsInfoDto() {
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

    public Long getTravelledDistance() {
        return travelledDistance;
    }

    public void setTravelledDistance(Long travelledDistance) {
        this.travelledDistance = travelledDistance;
    }

    public Set<PartNamePriceDto> getParts() {
        return parts;
    }

    public void setParts(Set<PartNamePriceDto> parts) {
        this.parts = parts;
    }
}
