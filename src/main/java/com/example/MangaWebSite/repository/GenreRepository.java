package com.example.MangaWebSite.repository;

import com.example.MangaWebSite.models.Genre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GenreRepository extends JpaRepository<Genre, Integer> {
    List<Genre> findByIdIn(List<Integer> genres);
}
