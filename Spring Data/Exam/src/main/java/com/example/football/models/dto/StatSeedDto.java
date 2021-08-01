package com.example.football.models.dto;

import javax.validation.constraints.Positive;

public class StatSeedDto {

    private float shooting;
    private float passing;
    private float endurance;

    public StatSeedDto() {
    }

    @Positive
    public float getShooting() {
        return shooting;
    }

    public void setShooting(float shooting) {
        this.shooting = shooting;
    }

    @Positive
    public float getPassing() {
        return passing;
    }

    public void setPassing(float passing) {
        this.passing = passing;
    }

    @Positive
    public float getEndurance() {
        return endurance;
    }

    public void setEndurance(float endurance) {
        this.endurance = endurance;
    }
}
