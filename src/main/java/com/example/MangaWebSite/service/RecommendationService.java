package com.example.MangaWebSite.service;


import ai.djl.util.Pair;
import com.example.MangaWebSite.models.Comics;
import com.example.MangaWebSite.models.Genre;
import com.example.MangaWebSite.repository.ComicsRepository;
import com.example.MangaWebSite.repository.GenreRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.example.MangaWebSite.models.Comics.computeCosineSimilarity;
import static com.example.MangaWebSite.models.Comics.deserializeFloatArray;

@Service
public class RecommendationService {

    private final ComicsRepository comicsRepository;
    private final EmbeddingService embeddingService;
    private final GenreRepository genreRepository;

    public RecommendationService(ComicsRepository comicsRepository, EmbeddingService embeddingService, GenreRepository genreRepository) {
        this.comicsRepository = comicsRepository;
        this.embeddingService = embeddingService;
        this.genreRepository = genreRepository;
    }

    public List<Comics> findSimilarComics(String query, int topN) throws Exception {
        // Отримуємо вбудовування (embedding) для запиту користувача
        float[] queryEmbedding = embeddingService.getEmbedding(query);

        // Завантажуємо всі комікси з бази даних
        List<Comics> allComics = comicsRepository.findAll();

        // Створюємо список для зберігання пар "комікс - схожість"
        List<Pair<Comics, Double>> similarComics = new ArrayList<>();

        // Отримуємо список усіх жанрів із бази даних
        List<String> allGenreNames = genreRepository.findAll()
                .stream().map(Genre::getName)
                .collect(Collectors.toList());

        // Вибираємо жанри, які згадуються в запиті користувача
        Set<String> mentionedGenreNames = allGenreNames.stream()
                .filter(genreName -> query.toLowerCase().contains(genreName.toLowerCase()))
                .collect(Collectors.toSet());

        // Обчислюємо схожість для кожного коміксу
        for (Comics comic : allComics) {
            if (comic.getDescriptionEmbedding() != null) {
                // Десеріалізуємо вбудовування коміксу
                float[] comicEmbedding = deserializeFloatArray(comic.getDescriptionEmbedding());

                // Обчислюємо косинусну схожість між запитом і коміксом
                double cosineSimilarity = computeCosineSimilarity(queryEmbedding, comicEmbedding);
                System.out.println(comic.getTitle() + " - cosineSimilarity " + cosineSimilarity);
                // Отримуємо жанри коміксу
                Set<String> comicGenreNames = comic.getGenres()
                        .stream().map(Genre::getName)
                        .collect(Collectors.toSet());

                // Рахуємо кількість збігів жанрів із запиту з жанрами коміксу
                int matchingGenres = (int) mentionedGenreNames.stream()
                        .filter(comicGenreNames::contains)
                        .count();

                // Остаточна схожість: косинусна схожість + бонус за збіги жанрів
                double finalSimilarity = cosineSimilarity + 0.1 * matchingGenres;

                // Додаємо комікс і його схожість у список
                similarComics.add(new Pair<>(comic, finalSimilarity));
            }
        }

        // Сортуємо комікси за спаданням схожості
        similarComics.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));

        // Визначаємо кількість коміксів для повернення (не більше topN і не більше наявних)
        int limit = Math.min(topN, similarComics.size());

        // Виводимо інформацію для дебагу
        System.out.println("Загальна кількість схожих коміксів: " + similarComics.size());
        System.out.println("Повертаємо топ " + limit + " рекомендацій.");

        // Встановлюємо поріг схожості для релевантності
        double similarityThreshold = 0.5; // Наприклад, комікс релевантний, якщо схожість > 0.5

        // Беремо топ-N рекомендацій
        List<Pair<Comics, Double>> topNRecommendations = similarComics.subList(0, limit);

        // Знаходимо всі релевантні комікси (з схожістю > порогу)
        List<Pair<Comics, Double>> relevantComics = similarComics.stream()
                .filter(pair -> pair.getValue() > similarityThreshold)
                .collect(Collectors.toList());

        // Обчислюємо precision@N
        long truePositives = topNRecommendations.stream()
                .filter(pair -> pair.getValue() > similarityThreshold)
                .count();
        double precision = limit > 0 ? (double) truePositives / limit : 0.0;

        // Обчислюємо recall@N
        double recall = relevantComics.size() > 0 ? (double) truePositives / relevantComics.size() : 0.0;

        // Обчислюємо F1@N
        double f1Score = (precision + recall > 0) ? 2 * (precision * recall) / (precision + recall) : 0.0;

        // Виводимо метрики
        System.out.println("Precision@" + limit + ": " + precision);
        System.out.println("Recall@" + limit + ": " + recall);
        System.out.println("F1@" + limit + ": " + f1Score);

        // Повертаємо список рекомендованих коміксів
        return topNRecommendations.stream()
                .map(Pair::getKey)
                .collect(Collectors.toList());
    }
}

