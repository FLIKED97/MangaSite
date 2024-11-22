package com.example.MangaWebSite.config;

import com.example.MangaWebSite.models.Comics;
import com.example.MangaWebSite.repository.ComicsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Configuration
@EnableScheduling
public class SchedulerConfig {

    private final ComicsRepository comicsRepository;

    @Autowired
    public SchedulerConfig(ComicsRepository comicsRepository) {
        this.comicsRepository = comicsRepository;
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

}

