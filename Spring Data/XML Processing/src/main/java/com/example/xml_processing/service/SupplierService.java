package com.example.xml_processing.service;

import com.example.xml_processing.model.entity.Supplier;

import java.io.IOException;


public interface SupplierService {
    void seedSuppliers() throws IOException;

    Supplier getRandomSupplier();

    long getCount();
}
