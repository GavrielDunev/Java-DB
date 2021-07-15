package com.example.springdataautomappingobjects.config;

import com.example.springdataautomappingobjects.model.dto.GameAddDto;
import com.example.springdataautomappingobjects.model.entity.Game;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class ApplicationConfiguration {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.typeMap(GameAddDto.class, Game.class)
                .addMappings(mapper -> mapper.map(GameAddDto::getThumbnailUrl, Game::setImageThumbnail));

        return modelMapper;
    }
}
