package com.example.MangaWebSite.service;

import com.example.MangaWebSite.models.*;
import com.example.MangaWebSite.repository.ComicsRepository;
import com.example.MangaWebSite.repository.GenreRepository;
import com.example.MangaWebSite.security.PersonDetails;
import lombok.AllArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
        comic.setCreatedAt(LocalDateTime.now());
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
        LocalDateTime oneMonthAgo = LocalDateTime.now().minusMonths(1);
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

    public Page<Comics> getNewCreatedComics(int page, int pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by("createdAt").descending());
        LocalDateTime oneMonthAgo = LocalDateTime.now().minusDays(31);
        return comicsRepository.findAllByCreatedAt(pageable, oneMonthAgo);
    }

    @Transactional(readOnly = true)
    public Page<Comics> getAllComicsWithNewChapter(int i) {
        Pageable pageable = PageRequest.of(i, 10, Sort.by("createdAt").descending());
        LocalDateTime oneMonthAgo = LocalDateTime.now().minusDays(70);
        return comicsRepository.findAllComicsWithNewChapters(oneMonthAgo, pageable);
    }

    public void incrementViewCount(Comics comic) {
        comic.incrementViewCount();
        comicsRepository.save(comic);
    }
}
