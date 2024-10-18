package com.example.MangaWebSite.service;

import com.example.MangaWebSite.models.Comics;
import com.example.MangaWebSite.models.Genre;
import com.example.MangaWebSite.repository.ComicsRepository;
import com.example.MangaWebSite.repository.GenreRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@AllArgsConstructor
public class ComicsService {

    private final ComicsRepository comicsRepository;
    private final GenreRepository genreRepository;

    public List<Comics> showAll() {
        return comicsRepository.findAll();
    }

    public void save(Comics comic) {
        comicsRepository.save(comic);
    }

    public Comics saveComic(Comics comic) {
        comic.setCreatedAt(new Date());  // Встановлюємо поточну дату
        return comicsRepository.save(comic);  // Зберігаємо комікс і повертаємо його зі збереженим ID
    }
    public void addGenresToComic(Comics comic, List<Integer> genreIds) {
        // Знаходимо жанри за їхніми ідентифікаторами
        List<Genre> genres = genreRepository.findByIdIn(genreIds);

        // Перевіряємо, чи знайдено жанри
        if (!genres.isEmpty()) {
            comic.setGenres(genres);  // Встановлюємо знайдені жанри для комікса
            comicsRepository.save(comic);  // Оновлюємо комікс з жанрами
        } else {
            System.out.println("Жанри не знайдено для ID: " + genreIds);
        }
    }

}
