package com.example.json_processing.service;

import com.example.json_processing.model.dto.SalesFullInfoDto;

import java.util.List;

public interface SaleService {
    void seedSales();

    List<SalesFullInfoDto> findAllSales();
}
