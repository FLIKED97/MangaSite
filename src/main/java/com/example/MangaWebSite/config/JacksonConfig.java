package com.example.MangaWebSite.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.StreamWriteConstraints;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

@Configuration
public class JacksonConfig {
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();

        // Реєстрація модуля для роботи з датами
        mapper.registerModule(new JavaTimeModule());
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        // Вимкнення помилки на порожніх бінах
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

        // Виключення null значень
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        // Додавання обмеження на глибину серіалізації
        mapper.getFactory().setStreamWriteConstraints(
                StreamWriteConstraints.builder().maxNestingDepth(100).build()
        );

        return mapper;
    }
    @Bean
    public Jackson2ObjectMapperBuilder jacksonBuilder() {
        Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();
        builder.failOnUnknownProperties(false);
        builder.serializationInclusion(JsonInclude.Include.NON_NULL);
        return builder;
    }
}
