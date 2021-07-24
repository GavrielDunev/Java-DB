package com.example.xml_processing.service.impl;

import com.example.xml_processing.model.entity.Supplier;
import com.example.xml_processing.repository.SupplierRepository;
import com.example.xml_processing.service.SupplierService;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class SupplierServiceImpl implements SupplierService {
    private final SupplierRepository supplierRepository;

    public SupplierServiceImpl(SupplierRepository supplierRepository) {
        this.supplierRepository = supplierRepository;
    }

    @Override
    public void seedSuppliers() throws IOException {

    }

    @Override
    public Supplier getRandomSupplier() {
        return null;
    }

    @Override
    public long getCount() {
        return this.supplierRepository.count();
    }
}
