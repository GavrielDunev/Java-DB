package com.example.football.service.impl;

import com.example.football.models.dto.StatSeedRootDto;
import com.example.football.models.entity.Stat;
import com.example.football.repository.StatRepository;
import com.example.football.service.StatService;
import com.example.football.util.ValidationUtil;
import com.example.football.util.XmlParser;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class StatServiceImpl implements StatService {

    private static final String STAST_FILE_PATH = "src/main/resources/files/xml/stats.xml";

    private final StatRepository statRepository;
    private final XmlParser xmlParser;
    private final ValidationUtil validationUtil;
    private final ModelMapper modelMapper;

    public StatServiceImpl(StatRepository statRepository, XmlParser xmlParser, ValidationUtil validationUtil, ModelMapper modelMapper) {
        this.statRepository = statRepository;
        this.xmlParser = xmlParser;
        this.validationUtil = validationUtil;
        this.modelMapper = modelMapper;
    }

    @Override
    public boolean areImported() {
        return this.statRepository.count() > 0;
    }

    @Override
    public String readStatsFileContent() throws IOException {
        return Files.readString(Path.of(STAST_FILE_PATH));
    }

    @Override
    public String importStats() throws JAXBException {
        StatSeedRootDto statSeedRootDto = xmlParser.fromFile(STAST_FILE_PATH, StatSeedRootDto.class);
        StringBuilder sb = new StringBuilder();

        statSeedRootDto.getStats()
                .stream()
                .filter(statSeedDto -> {
                    boolean isValid = this.validationUtil.isValid(statSeedDto)
                            && !isStatExisting(statSeedDto.getPassing(),
                            statSeedDto.getShooting(), statSeedDto.getEndurance());

                    sb.append(isValid ? String.format("Successfully imported Stat %.2f - %.2f - %.2f",
                            statSeedDto.getPassing(), statSeedDto.getShooting(), statSeedDto.getEndurance())
                            : "Invalid Stat")
                            .append(System.lineSeparator());

                    return isValid;
                })
                .map(statSeedDto -> modelMapper.map(statSeedDto, Stat.class))
                .forEach(this.statRepository::save);

        return sb.toString();
    }

    @Override
    public boolean isStatExisting(float passing, float shooting, float endurance) {
        return this.statRepository.existsByPassingAndShootingAndEndurance(passing, shooting, endurance);
    }

    @Override
    public Stat findById(Long id) {
        return this.statRepository.findById(id).orElse(null);
    }
}
