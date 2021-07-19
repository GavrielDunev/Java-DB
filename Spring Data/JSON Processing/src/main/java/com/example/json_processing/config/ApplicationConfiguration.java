package com.example.json_processing.config;

import com.example.json_processing.model.dto.CategoriesByProductsDto;
import com.example.json_processing.model.dto.CustomerTotalSalesInfoDto;
import com.example.json_processing.model.entity.Category;
import com.example.json_processing.model.entity.Customer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.spi.MappingContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Configuration
public class ApplicationConfiguration {
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        Converter<String, LocalDate> stringToLocalDateConverter = new Converter<String, LocalDate>() {
            @Override
            public LocalDate convert(MappingContext<String, LocalDate> context) {
                DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
                return LocalDate.parse(context.getSource(), format);
            }
        };

        modelMapper.addConverter(stringToLocalDateConverter);

        modelMapper.typeMap(Category.class, CategoriesByProductsDto.class)
                .addMappings(mapping -> mapping.map(Category::getName, CategoriesByProductsDto::setCategory));

        modelMapper.typeMap(Customer.class, CustomerTotalSalesInfoDto.class)
                .addMappings(mapping -> mapping.map(Customer::getName, CustomerTotalSalesInfoDto::setFullName));

        return modelMapper;
    }

    @Bean
    public Gson gson() {
        return new GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation()
                .setPrettyPrinting()
                .create();
    }
}
