package com.example.json_processing.service.impl;

import com.example.json_processing.model.dto.PartSeedDto;
import com.example.json_processing.model.entity.Part;
import com.example.json_processing.repository.PartRepository;
import com.example.json_processing.service.PartService;
import com.example.json_processing.service.SupplierService;
import com.example.json_processing.util.ValidationUtil;
import com.google.gson.Gson;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import static com.example.json_processing.constant.GlobalConstants.RESOURCES_FILE_PATH;

@Service
public class PartServiceImpl implements PartService {
    private static final String PARTS_FILE_NAME = "parts.json";

    private final PartRepository partRepository;
    private final Gson gson;
    private final ValidationUtil validationUtil;
    private final ModelMapper modelMapper;
    private final SupplierService supplierService;

    public PartServiceImpl(PartRepository partRepository, Gson gson, ValidationUtil validationUtil, ModelMapper modelMapper, SupplierService supplierService) {
        this.partRepository = partRepository;
        this.gson = gson;
        this.validationUtil = validationUtil;
        this.modelMapper = modelMapper;
        this.supplierService = supplierService;
    }

    @Override
    public void seedParts() throws IOException {
        if (this.partRepository.count() > 0) {
            return;
        }

        PartSeedDto[] partSeedDtos = gson.fromJson(Files.readString(Path.of(RESOURCES_FILE_PATH + PARTS_FILE_NAME)),
                PartSeedDto[].class);
        Arrays.stream(partSeedDtos)
                .filter(validationUtil::isValid)
                .map(partSeedDto -> {
                    Part part = modelMapper.map(partSeedDto, Part.class);
                    part.setSupplier(this.supplierService.getRandomSupplier());
                    return part;
                })
                .forEach(this.partRepository::save);

    }

    @Override
    public Set<Part> getRandomParts() {
        int numberOfPartsToGet = ThreadLocalRandom.current().nextInt(3, 6);
        long count = this.partRepository.count();
        Set<Part> parts = new HashSet<>();

        for (int i = 0; i < numberOfPartsToGet; i++) {
            long randomId = ThreadLocalRandom.current().nextLong(1, count + 1);
            parts.add(this.partRepository.findById(randomId).orElse(null));
        }

        return parts;
    }
}
