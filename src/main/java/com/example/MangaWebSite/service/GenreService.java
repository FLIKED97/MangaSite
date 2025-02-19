package com.example.MangaWebSite.service;

import com.example.MangaWebSite.models.Genre;
import com.example.MangaWebSite.repository.GenreRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
@Transactional
public class GenreService {
    private final GenreRepository genreRepository;

    public List<Genre> findAll() {
        return genreRepository.findAll();
    }

    public List<Genre> findByIds(List<Integer> genres) {
        return genreRepository.findByIdIn(genres);
    }

    public List<Genre> findByComicsId(int id) {
    return genreRepository.findByComicsId(id);
    }
}
