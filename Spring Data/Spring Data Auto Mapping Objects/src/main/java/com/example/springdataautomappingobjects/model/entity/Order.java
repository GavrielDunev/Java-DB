package com.example.springdataautomappingobjects.model.entity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
public class Order extends BaseEntity{
    private User buyer;
    private List<Game> products;

    public Order() {
        this.products = new ArrayList<>();
    }

    @OneToOne()
    public User getBuyer() {
        return buyer;
    }

    public void setBuyer(User user) {
        this.buyer = user;
    }

    @ManyToMany(fetch = FetchType.EAGER)
    public List<Game> getProducts() {
        return products;
    }

    public void setProducts(List<Game> products) {
        this.products = products;
    }
}
