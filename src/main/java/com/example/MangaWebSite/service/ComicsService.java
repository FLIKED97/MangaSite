package com.example.MangaWebSite.service;

import com.example.MangaWebSite.models.*;
import com.example.MangaWebSite.repository.ComicsRepository;
import com.example.MangaWebSite.repository.GenreRepository;
import com.example.MangaWebSite.repository.ReadingProgressRepository;
import lombok.AllArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;


@Service
@AllArgsConstructor
@Transactional
public class ComicsService {

    private final ComicsRepository comicsRepository;
    private final GenreRepository genreRepository;

    private final ReadingProgressRepository readingProgressRepository;


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

    public List<Comics> getPopularComics(int page, int pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by("viewCount").descending());
        return comicsRepository.findAll(pageable).getContent();
    }

    public List<Comics> getPopularComicsWithNewChapters(double threshold) {
        LocalDateTime oneMonthAgo = LocalDateTime.now().minusMonths(1);
        return comicsRepository.findPopularComicsWithNewChapters(threshold, oneMonthAgo);
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

    public void incrementViewCount(Comics comic) {
        comic.incrementViewCount();
        comicsRepository.save(comic);
    }

    @Transactional
    public List<Comics> searchByTitle(String term) {
        return comicsRepository.findByTitleContaining(term);
    }

    @Transactional
    public List<Comics> searchByAuthor(String term) {
        return comicsRepository.findByAuthorContaining(term);
    }

    public List<Comics> getNewCreatedComics(int page, int pageSize, int day) {
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by("createdAt").descending());
        LocalDateTime oneMonthAgo = LocalDateTime.now().minusDays(day);
        return comicsRepository.findAllByCreatedAt(pageable, oneMonthAgo).getContent();
    }

    @Transactional(readOnly = true)
    public Page<Comics> getAllComicsWithNewChapter(int i) {
        Pageable pageable = PageRequest.of(i, 10, Sort.by("createdAt").descending());
        LocalDateTime oneMonthAgo = LocalDateTime.now().minusDays(70);
        return comicsRepository.findAllComicsWithNewChapters(oneMonthAgo, pageable);
    }
    public List<Comics> getCurrentlyPopularReading(int page, int pageSize, int day) {
        Pageable pageable = PageRequest.of(page, pageSize);
        LocalDateTime cutoffTime = LocalDateTime.now().minusDays(day);
        return readingProgressRepository.findCurrentlyPopularReading(cutoffTime, pageable).getContent();
    }

    public List<Comics> getCurrentlyReading(int page, int pageSize, int day) {
        Pageable pageable = PageRequest.of(page, pageSize);
        LocalDateTime cutoffTime = LocalDateTime.now().minusDays(day); // Активність за останні 30 хв
        return readingProgressRepository.findCurrentlyReading(cutoffTime, pageable);
    }
}
