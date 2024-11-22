package com.example.MangaWebSite.service;

import com.example.MangaWebSite.models.*;
import com.example.MangaWebSite.repository.ComicsRepository;
import com.example.MangaWebSite.repository.GenreRepository;
import lombok.AllArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;


@Service
@AllArgsConstructor
@Transactional

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
        comic.setCreatedAt(LocalDate.now());
        return comicsRepository.save(comic);  // Зберігаємо комікс і повертаємо його зі збереженим ID //TODO Переробити щоб створювати комікси могли тільки ROLE_PUBLISHER
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

    public Comics getComicById(int id) {
        return comicsRepository.findById(id).orElse(null);  // Повертаємо комікс або null
    }


    public List<Comics> getComicsByTabId(int tabId) {
        return comicsRepository.findAllByTabsId(tabId);
    }

    public List<Comics> getComicsSortedBy(String sortBy) {
        List<Comics> comicsList = comicsRepository.findAll();

        if ("rating".equals(sortBy)) {
            comicsList.sort((c1, c2) -> Double.compare(c2.getAverageRating(), c1.getAverageRating()));
        } else if ("views".equals(sortBy)) {
            comicsList.sort(Comparator.comparingInt(Comics::getViewCount).reversed());
        }
        return comicsList;
    }
    private double calculateAverageRating(Comics comics) {
        List<Rating> ratings = comics.getRatings();
        return ratings.isEmpty() ? 0 : ratings.stream().mapToInt(Rating::getRating).average().orElse(0);
    }

    public List<Comics> getPopularComics() {
        return comicsRepository.findAllByOrderByViewCountDesc();
    }

    public List<Comics> getPopularComicsWithNewChapters(double threshold) {
        LocalDate oneMonthAgo = LocalDate.now().minusMonths(1);
        return comicsRepository.findPopularComicsWithNewChapters(threshold, oneMonthAgo);
    }

    public List<Comics> getRecentlyRead(Person person) {
        return null;
    }

    public List<Comics> getComicsByGenresAndSort(List<Integer> genres, String sortBy) {

        List<Genre> genresName = genreRepository.findByIdIn(genres);
        List<Comics> comicsList = comicsRepository.findAllComicsByGenres(genresName);

        if ("rating".equals(sortBy)) {
            comicsList.sort((c1, c2) -> Double.compare(c2.getAverageRating(), c1.getAverageRating()));
        } else if ("views".equals(sortBy)) {
            comicsList.sort(Comparator.comparingInt(Comics::getViewCount).reversed());
        }
        return comicsList;
    }
}
