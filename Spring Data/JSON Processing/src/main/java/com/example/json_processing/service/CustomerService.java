package com.example.json_processing.service;

import com.example.json_processing.model.dto.CustomerTotalSalesInfoDto;
import com.example.json_processing.model.dto.OrderedCustomersInfoDto;
import com.example.json_processing.model.entity.Customer;

import java.io.IOException;
import java.util.List;

public interface CustomerService {
    void seedCustomers() throws IOException;

    Customer getRandomCustomer();

    List<OrderedCustomersInfoDto> findAllOrderedByBirthDate();

    List<CustomerTotalSalesInfoDto> findAllWithBoughtCar();
}
