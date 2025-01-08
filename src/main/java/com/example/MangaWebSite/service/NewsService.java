package com.example.MangaWebSite.service;

import com.example.MangaWebSite.models.Category;
import com.example.MangaWebSite.models.News;
import com.example.MangaWebSite.repository.NewsRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NewsService {

    private final NewsRepository newsRepository;

    public List<News> getAllNews() {
        return newsRepository.findAll();
    }

    public List<News> getPublishedNews() {
        return newsRepository.findByIsPublished(true);
    }

    public List<News> getNewsByCategory(int categoryId) {
        return newsRepository.findByCategory_Id(categoryId);
    }

    public News createNews(News news) {
        return newsRepository.save(news);
    }

    public List<News> getAllPublishedNews() {
        return newsRepository.findAllByIsPublishedTrueOrderByCreatedAtDesc();
    }
    public News getNewsById(int id) {
        return newsRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Новина не знайдена"));
    }

    public Page<News> findByCategory(Category category, Pageable pageable) {
        return newsRepository.findByCategory(category, pageable);
    }

    public Page<News> findAll(Pageable pageable) {
        return newsRepository.findAll(pageable);
    }
}

