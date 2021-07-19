package com.example.json_processing.service.impl;

import com.example.json_processing.model.dto.SalesFullInfoDto;
import com.example.json_processing.model.entity.Sale;
import com.example.json_processing.repository.SaleRepository;
import com.example.json_processing.service.CarService;
import com.example.json_processing.service.CustomerService;
import com.example.json_processing.service.SaleService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Service
public class SaleServiceImpl implements SaleService {
    private final double[] DISCOUNTS = new double[]{0, 0.05, 0.1, 0.15, 0.2, 0.3, 0.4, 0.5};

    private final SaleRepository saleRepository;
    private final CarService carService;
    private final CustomerService customerService;
    private final ModelMapper modelMapper;

    public SaleServiceImpl(SaleRepository saleRepository, CarService carService, CustomerService customerService, ModelMapper modelMapper) {
        this.saleRepository = saleRepository;
        this.carService = carService;
        this.customerService = customerService;
        this.modelMapper = modelMapper;
    }

    @Override
    public void seedSales() {
        if (this.saleRepository.count() > 0) {
            return;
        }

        for (int i = 0; i < 19; i++) {
            Sale sale = new Sale();

            sale.setCar(this.carService.getRandomCar());
            sale.setCustomer(this.customerService.getRandomCustomer());
            sale.setDiscount(getRandomDiscount());

            this.saleRepository.save(sale);
        }
    }

    @Override
    public List<SalesFullInfoDto> findAllSales() {
        return this.saleRepository.findAll()
                .stream()
                .map(sale -> {
                    SalesFullInfoDto mappedSale = modelMapper.map(sale, SalesFullInfoDto.class);

                    if (sale.getCustomer().isYoungDriver()) {
                        mappedSale.setDiscount(mappedSale.getDiscount() + 0.05);
                    }

                    double price = sale.getCar().getParts().stream()
                            .mapToDouble(part -> Double.parseDouble(String.valueOf(part.getPrice()))).sum();

                    double priceWithDiscount = price - (price * mappedSale.getDiscount());

                    mappedSale.setPrice(BigDecimal.valueOf(price));
                    mappedSale.setPriceWithDiscount(BigDecimal.valueOf(priceWithDiscount));

                    return mappedSale;
                }).collect(Collectors.toList());
    }

    private double getRandomDiscount() {
        int randomIndex = ThreadLocalRandom.current().nextInt(0, DISCOUNTS.length);
        return DISCOUNTS[randomIndex];
    }
}
