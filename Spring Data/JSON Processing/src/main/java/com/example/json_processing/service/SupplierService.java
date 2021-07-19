package com.example.json_processing.service;

import com.example.json_processing.model.dto.LocalSuppliersInfoDto;
import com.example.json_processing.model.entity.Supplier;

import java.io.IOException;
import java.util.List;

public interface SupplierService {
    void seedSuppliers() throws IOException;

    Supplier getRandomSupplier();

    List<LocalSuppliersInfoDto> findAllNotImporters();
}
