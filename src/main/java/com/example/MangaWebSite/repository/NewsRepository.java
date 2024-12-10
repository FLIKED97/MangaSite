package com.example.MangaWebSite.repository;

import com.example.MangaWebSite.models.News;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NewsRepository extends JpaRepository<News, Integer> {
    List<News> findByCategory_Id(int categoryId);
    List<News> findByIsPublished(boolean isPublished);

    List<News> findAllByIsPublishedTrueOrderByCreatedAtDesc();
}

