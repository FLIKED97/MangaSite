package com.example.MangaWebSite.serializer;

import com.example.MangaWebSite.models.Comics;
import com.example.MangaWebSite.models.Genre;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.util.Base64;

public class ComicsSerializer extends JsonSerializer<Comics> {
    @Override
    public void serialize(Comics comics, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();

        // Серіалізуємо базові поля
        jsonGenerator.writeNumberField("id", comics.getId());
        jsonGenerator.writeStringField("title", comics.getTitle());
        jsonGenerator.writeStringField("author", comics.getAuthor());
        jsonGenerator.writeStringField("description", comics.getDescription());
        jsonGenerator.writeStringField("status", comics.getStatus());

        // Серіалізуємо зображення
        if (comics.getCoverImage() != null) {
            jsonGenerator.writeStringField("coverImageBase64",
                    Base64.getEncoder().encodeToString(comics.getCoverImage()));
        }

        // Серіалізуємо жанри
        if (comics.getGenres() != null) {
            jsonGenerator.writeArrayFieldStart("genres");
            for (Genre genre : comics.getGenres()) {
                jsonGenerator.writeString(genre.getName());
            }
            jsonGenerator.writeEndArray();
        }

        // Серіалізуємо createdAt
        if (comics.getCreatedAt() != null) {
            jsonGenerator.writeStringField("createdAt", comics.getCreatedAt().toString());
        }

        // Додаткові поля
        jsonGenerator.writeNumberField("viewCount", comics.getViewCount());
        jsonGenerator.writeNumberField("popularityRating", comics.getPopularityRating());

        jsonGenerator.writeEndObject();
    }
}