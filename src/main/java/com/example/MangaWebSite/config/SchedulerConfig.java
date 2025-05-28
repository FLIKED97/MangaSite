package com.example.MangaWebSite.config;

import com.example.MangaWebSite.models.Comics;
import com.example.MangaWebSite.models.ComicsUtil;
import com.example.MangaWebSite.repository.ComicsRepository;
import com.example.MangaWebSite.service.ComicsService;
import com.example.MangaWebSite.service.EmbeddingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Configuration
@EnableScheduling
@Component
public class SchedulerConfig {

    private final ComicsRepository comicsRepository;
    private final EmbeddingService embeddingService;

    @Autowired
    public SchedulerConfig(ComicsRepository comicsRepository, EmbeddingService embeddingService) {
        this.comicsRepository = comicsRepository;
        this.embeddingService = embeddingService;
    }

    @Scheduled(cron = "0 43 16 * * *")
    @Transactional
    public void updateComicsPopularityRatings() {
        int pageSize = 100; // Розмір сторінки
        int page = 0;

        Page<Comics> comicsPage;
        do {
            comicsPage = comicsRepository.findAll(PageRequest.of(page, pageSize));
            for (Comics comic : comicsPage) {
                comic.calculatePopularityRating();
                comicsRepository.save(comic);
            }
            page++;
        } while (!comicsPage.isLast());

        System.out.println("Updated popularity ratings for all comics in batches.");
    }
    // Запуск щодня о 03:00 ранку
//    @Scheduled(cron = "0 0 3 * * *")
//    public void updateEmbeddings() throws Exception {
//        List<Comics> allComics = comicsRepository.findAll();
//        for (Comics comic : allComics) {
//            if (comic.getDescription() != null && !comic.getDescription().isEmpty()) {
//                float[] embedding = embeddingService.getEmbedding(comic.getDescription());
//                byte[] embeddingBytes = ComicsService.serializeFloatArray(embedding);
//                comic.setDescriptionEmbedding(embeddingBytes);
//                comicsRepository.save(comic);
//            }
//        }
//        System.out.println("Embeddings updated.");
//    }

    // Запускаємо щодня о 03:00 ранку
    @Scheduled(cron = "0 0 3 * * *")
    public void updateEmbeddings() throws Exception {
        List<Comics> allComics = comicsRepository.findAll();
        for (Comics comic : allComics) {
            if (comic.getDescription() != null && !comic.getDescription().isEmpty()) {
                // Якщо поле ембеддінгу порожнє або застаріле – оновлюємо його
                if (comic.getDescriptionEmbedding() == null || isOutdated(comic)) {
                    float[] embedding = embeddingService.getEmbedding(comic.getDescription());
                    byte[] embeddingBytes = ComicsUtil.serializeFloatArray(embedding);
                    comic.setDescriptionEmbedding(embeddingBytes);
                    comicsRepository.save(comic);
                }
            }
        }
        System.out.println("Embeddings updated.");
    }

    // Простий приклад логіки перевірки "застарілості"
    private boolean isOutdated(Comics comic) {
        // Наприклад, якщо розмір ембеддінгу не відповідає очікуваному (наприклад, 768 розмірність * 4 байти)
        byte[] embedding = comic.getDescriptionEmbedding();
        int expectedLength = 768 * 4; // замініть 768 на фактичну розмірність моделі, якщо потрібно
        return embedding.length != expectedLength;
    }
}

