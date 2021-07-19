package com.example.json_processing.service;

import com.example.json_processing.model.entity.Part;

import java.io.IOException;
import java.util.Set;

public interface PartService {
    void seedParts() throws IOException;

    Set<Part> getRandomParts();
}
