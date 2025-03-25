package com.example.MangaWebSite.serializer;

import com.example.MangaWebSite.models.Comics;
import com.example.MangaWebSite.models.Genre;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.util.Base64;
import java.util.Optional;

public class ComicsSerializer extends JsonSerializer<Comics> {
    @Override
    public void serialize(Comics comics, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();

        jsonGenerator.writeNumberField("id", comics.getId());
        jsonGenerator.writeStringField("title", comics.getTitle());
        jsonGenerator.writeStringField("author", comics.getAuthor());
        jsonGenerator.writeStringField("description", comics.getDescription());
        jsonGenerator.writeStringField("status", comics.getStatus());

        // Серіалізація comicsType (якщо використовується для внутрішньої логіки)
        if (comics.getComicsType() != null) {
            jsonGenerator.writeStringField("comicsType", comics.getComicsType().name());
            jsonGenerator.writeStringField("comicsTypeDisplay", comics.getComicsType().getDisplayName());
        }

        // Серіалізація publicationType для розрізнення типів публікацій
        if (comics.getPublicationType() != null) {
            jsonGenerator.writeStringField("publicationType", comics.getPublicationType().name());
            jsonGenerator.writeStringField("publicationTypeDisplay", comics.getPublicationType().getDisplayName());
        }

        // Безпечна серіалізація зображення
        jsonGenerator.writeStringField("coverImageBase64",
                Optional.ofNullable(comics.getSafeCoverImage())
                        .map(image -> Base64.getEncoder().encodeToString(image))
                        .orElse(null)
        );

        // Серіалізуємо жанри
        if (comics.getGenres() != null && !comics.getGenres().isEmpty()) {
            jsonGenerator.writeArrayFieldStart("genres");
            for (Genre genre : comics.getGenres()) {
                if (genre != null) {
                    jsonGenerator.writeString(genre.getName());
                }
            }
            jsonGenerator.writeEndArray();
        } else {
            jsonGenerator.writeArrayFieldStart("genres");
            jsonGenerator.writeEndArray();
        }

        // Серіалізуємо createdAt
        if (comics.getCreatedAt() != null) {
            jsonGenerator.writeStringField("createdAt", comics.getCreatedAt().toString());
        }

        // Додаткові поля
        jsonGenerator.writeNumberField("viewCount", comics.getViewCount());
        jsonGenerator.writeNumberField("popularityRating", comics.getPopularityRating());

        // Додаємо середній рейтинг
        double averageRating = comics.getAverageRating();
        jsonGenerator.writeNumberField("averageRating", averageRating);

        jsonGenerator.writeEndObject();
    }
}
