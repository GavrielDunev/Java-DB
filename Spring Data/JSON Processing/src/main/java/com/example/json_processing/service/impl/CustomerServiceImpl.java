package com.example.json_processing.service.impl;

import com.example.json_processing.model.dto.CustomerSeedDto;
import com.example.json_processing.model.dto.CustomerTotalSalesInfoDto;
import com.example.json_processing.model.dto.OrderedCustomersInfoDto;
import com.example.json_processing.model.entity.Car;
import com.example.json_processing.model.entity.Customer;
import com.example.json_processing.model.entity.Part;
import com.example.json_processing.model.entity.Sale;
import com.example.json_processing.repository.CustomerRepository;
import com.example.json_processing.service.CustomerService;
import com.google.gson.Gson;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.example.json_processing.constant.GlobalConstants.RESOURCES_FILE_PATH;

@Service
public class CustomerServiceImpl implements CustomerService {
    private static final String CUSTOMER_FILE_NAME = "customers.json";

    private final CustomerRepository customerRepository;
    private final Gson gson;
    private final ModelMapper modelMapper;

    public CustomerServiceImpl(CustomerRepository customerRepository, Gson gson, ModelMapper modelMapper) {
        this.customerRepository = customerRepository;
        this.gson = gson;
        this.modelMapper = modelMapper;
    }


    @Override
    public void seedCustomers() throws IOException {
        if (this.customerRepository.count() > 0) {
            return;
        }

        Arrays.stream(gson.fromJson(Files.readString(Path.of(RESOURCES_FILE_PATH + CUSTOMER_FILE_NAME)),
                CustomerSeedDto[].class))
                .map(customerSeedDto -> modelMapper.map(customerSeedDto, Customer.class))
                .forEach(this.customerRepository::save);

    }

    @Override
    public Customer getRandomCustomer() {
        long randomId = ThreadLocalRandom.current().nextLong(1, this.customerRepository.count() + 1);
        return this.customerRepository.findById(randomId).orElse(null);
    }

    @Override
    public List<OrderedCustomersInfoDto> findAllOrderedByBirthDate() {
        return this.customerRepository.findAllOrderByBirthDateYoungDriver()
                .stream()
                .map(customer -> modelMapper.map(customer, OrderedCustomersInfoDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<CustomerTotalSalesInfoDto> findAllWithBoughtCar() {
        List<Customer> customers = this.customerRepository.findAllWithMoreThanOneCarBought();

        return customers.stream()
                .map(customer -> {
                    CustomerTotalSalesInfoDto mappedCustomer = modelMapper.map(customer, CustomerTotalSalesInfoDto.class);

                    Set<Sale> sales = customer.getSales();

                    List<Car> cars = sales.stream()
                            .map(Sale::getCar)
                            .collect(Collectors.toList());

                    double totalSum = cars.stream()
                            .mapToDouble(car -> {
                                return car.getParts().stream()
                                        .mapToDouble(part -> Double.parseDouble(String.valueOf(part.getPrice())))
                                        .sum();
                            })
                            .sum();

                    mappedCustomer.setBoughtCars(customer.getSales().size());
                    mappedCustomer.setSpentMoney(BigDecimal.valueOf(totalSum));
                    return mappedCustomer;
                })
                .collect(Collectors.toList());
    }
}
