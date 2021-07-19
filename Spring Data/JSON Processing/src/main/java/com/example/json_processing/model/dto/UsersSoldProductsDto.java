package com.example.json_processing.model.dto;

import com.google.gson.annotations.Expose;

import java.util.Set;

public class UsersSoldProductsDto {
    @Expose
    private String firstName;
    @Expose
    private String lastName;
    @Expose
    private Set<ProductsWithBuyerDto> soldProducts;

    public UsersSoldProductsDto() {
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Set<ProductsWithBuyerDto> getSoldProducts() {
        return soldProducts;
    }

    public void setSoldProducts(Set<ProductsWithBuyerDto> soldProducts) {
        this.soldProducts = soldProducts;
    }
}
