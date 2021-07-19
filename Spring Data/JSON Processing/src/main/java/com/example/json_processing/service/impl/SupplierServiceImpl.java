package com.example.json_processing.service.impl;

import com.example.json_processing.model.dto.LocalSuppliersInfoDto;
import com.example.json_processing.model.dto.SupplierSeedDto;
import com.example.json_processing.model.entity.Supplier;
import com.example.json_processing.repository.SupplierRepository;
import com.example.json_processing.service.SupplierService;
import com.google.gson.Gson;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import static com.example.json_processing.constant.GlobalConstants.RESOURCES_FILE_PATH;

@Service
public class SupplierServiceImpl implements SupplierService {
    private static final String SUPPLIERS_FILE_NAME = "suppliers.json";
    private final Gson gson;
    private final ModelMapper modelMapper;
    private final SupplierRepository supplierRepository;

    public SupplierServiceImpl(Gson gson, ModelMapper modelMapper, SupplierRepository supplierRepository) {
        this.gson = gson;
        this.modelMapper = modelMapper;
        this.supplierRepository = supplierRepository;
    }

    @Override
    public void seedSuppliers() throws IOException {
        if (this.supplierRepository.count() > 0) {
            return;
        }

        Arrays.stream(gson.fromJson(Files.readString(Path.of(RESOURCES_FILE_PATH + SUPPLIERS_FILE_NAME)),
                SupplierSeedDto[].class))
                .map(supplierSeedDto -> modelMapper.map(supplierSeedDto, Supplier.class))
                .forEach(this.supplierRepository::save);
    }

    @Override
    public Supplier getRandomSupplier() {
        long randomId = ThreadLocalRandom.current().nextLong(1, this.supplierRepository.count() + 1);
        return this.supplierRepository.findById(randomId).orElse(null);
    }

    @Override
    public List<LocalSuppliersInfoDto> findAllNotImporters() {
        return this.supplierRepository.findAllByImporterFalse()
                .stream()
                .map(supplier -> {
                    LocalSuppliersInfoDto localSupplier = modelMapper.map(supplier, LocalSuppliersInfoDto.class);
                    localSupplier.setPartsCount(supplier.getParts().size());
                    return localSupplier;
                })
                .collect(Collectors.toList());
    }
}
