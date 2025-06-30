package com.example.MangaWebSite.controller;

import com.example.MangaWebSite.service.UkrainianBookScraperService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final UkrainianBookScraperService scraperService;

    @Override
    public void run(String... args) throws Exception {
        // При першому запуску розкоментуй цей рядок, щоб додати 100 книжок:
        //scraperService.scrapeAndSaveUkrainianBooks(100);

        // Після того, як дані вперше завантажаться,
        // закоментуй виклик, щоб уникнути дублювання.
    }
}