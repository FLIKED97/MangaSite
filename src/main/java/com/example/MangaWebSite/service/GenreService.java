package com.example.MangaWebSite.service;

import com.example.MangaWebSite.models.Genre;
import com.example.MangaWebSite.repository.GenreRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class GenreService {
    private final GenreRepository genreRepository;

    public List<Genre> findAll() {
        return genreRepository.findAll();
    }

    public List<Genre> findByIds(List<Integer> genres) {
        return genreRepository.findByIdIn(genres);
    }
}
