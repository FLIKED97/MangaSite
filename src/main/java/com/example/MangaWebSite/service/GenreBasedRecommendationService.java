package com.example.MangaWebSite.service;

import com.example.MangaWebSite.models.*;
import com.example.MangaWebSite.repository.ComicsRepository;
import com.example.MangaWebSite.repository.PersonRepository;
import com.example.MangaWebSite.repository.ReadingProgressRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class GenreBasedRecommendationService {


    private ReadingProgressRepository readingProgressRepository;
    private PersonRepository personRepository;

    private ComicsRepository comicsRepository;

    public List<Comics> getRecommendationsBySimilarGenres(int userId) {
        // Отримання останніх 5 записів з читання
        List<ReadingProgress> recentProgress = readingProgressRepository.findTop5ByPersonIdOrderByUpdatedAtDesc(userId);
        // Набір жанрів з останніх прочитаних коміксів
        Set<Genre> genreSet = new HashSet<>();
        // Список ID коміксів, які вже знайдені (для виключення)
        Set<Integer> excludedComicIds = new HashSet<>();

        // Обробка прочитаних записів
        for (ReadingProgress progress : recentProgress) {
            Comics comic = progress.getChapter().getComics();
            if (comic != null) {
                excludedComicIds.add(comic.getId());
                if (comic.getGenres() != null) {
                    genreSet.addAll(comic.getGenres());
                }
            }
        }

        // Отримання закладок користувача
        Person person = personRepository.findById(userId).orElse(null);
        if (person != null && person.getTabs() != null) {
            for (Tabs tab : person.getTabs()) {
                if (tab.getComics() != null) {
                    for (Comics comic : tab.getComics()) {
                        excludedComicIds.add(comic.getId());
                        if (comic.getGenres() != null) {
                            genreSet.addAll(comic.getGenres());
                        }
                    }
                }
            }
        }

        // Якщо не знайдено жодного жанру – повернути популярні комікси, виключаючи вже прочитані/закладені
        if (genreSet.isEmpty()) {
            return comicsRepository.findTop5ByIdNotInOrderByPopularityRatingDesc(new ArrayList<>(excludedComicIds));
        }

        List<Genre> genres = new ArrayList<>(genreSet);
        // Пошук коміксів, які належать хоча б до одного з вибраних жанрів
        return comicsRepository.findDistinctTop5ByGenresInAndIdNotInOrderByPopularityRatingDesc(genres, new ArrayList<>(excludedComicIds));
    }
}
