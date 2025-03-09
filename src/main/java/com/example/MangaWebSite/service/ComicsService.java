package com.example.MangaWebSite.service;

import com.example.MangaWebSite.models.*;
import com.example.MangaWebSite.repository.ComicsRepository;
import com.example.MangaWebSite.repository.GenreRepository;
import com.example.MangaWebSite.repository.ReadingProgressRepository;
import jakarta.persistence.criteria.*;
import lombok.AllArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


@Service
@AllArgsConstructor
@Transactional
@Slf4j
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

    @Transactional
    public List<Comics> getComicsSortedBy(String sortBy) {
        List<Comics> comics = comicsRepository.findAll();
        if ("views".equals(sortBy)) {
            comics.sort((c1, c2) -> Integer.compare(c2.getViewCount(), c1.getViewCount()));
        } else {
            comics.sort((c1, c2) -> Double.compare(c2.getAverageRating(), c1.getAverageRating()));
        }
        return comics;
    }

    private double calculateAverageRating(Comics comics) {
        List<Rating> ratings = comics.getRatings();
        return ratings.isEmpty() ? 0 : ratings.stream().mapToInt(Rating::getRating).average().orElse(0);
    }

    public List<Comics> getPopularComics(int page, int pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by("viewCount").descending());
        return comicsRepository.findAll(pageable).getContent();
    }
    @Transactional(readOnly = true)
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

    @Transactional(readOnly = true)
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
    @Transactional(readOnly = true)
    public List<Comics> getCurrentlyPopularReading(int page, int pageSize, int day) {
        Pageable pageable = PageRequest.of(page, pageSize);
        LocalDateTime cutoffTime = LocalDateTime.now().minusDays(day);
        return readingProgressRepository.findCurrentlyPopularReading(cutoffTime, pageable).getContent();
    }
    @Transactional(readOnly = true)
    public List<Comics> getCurrentlyReading(int page, int pageSize, int day) {
        Pageable pageable = PageRequest.of(page, pageSize);
        LocalDateTime cutoffTime = LocalDateTime.now().minusDays(day); // Активність за останні 30 хв
        return readingProgressRepository.findCurrentlyReading(cutoffTime, pageable);
    }

    public List<Integer> getAllComicIds() {
        return comicsRepository.findAllComicIds(); // Запит до репозиторія
    }

    public List<Comics> findComicsByPersonId(int id) {
        return comicsRepository.findAllByPersonId(id);
    }
    @Transactional(readOnly = true)
    public Page<Comics> searchComics(String search, String sortBy, int page, int size,
                                     String genres, String comicsTypes, Integer minChapters, Integer maxChapters) {
        Pageable pageable = PageRequest.of(page, size, getSort(sortBy));
        Specification<Comics> spec = (root, query, cb) -> {
            assert query != null;
            query.distinct(true);  // Встановлюємо distinct всередині лямбди

            List<Predicate> predicates = new ArrayList<>();

            // Пошук за назвою (якщо не порожня)
            if (search != null && !search.isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("title")), "%" + search.toLowerCase() + "%"));
            }

            // Фільтр за типами коміксів
            if (comicsTypes != null && !comicsTypes.isEmpty()) {
                List<ComicsType> types = Arrays.stream(comicsTypes.split(","))
                        .map(String::trim)
                        .map(ComicsType::valueOf)  // Перетворюємо строкове представлення в enum
                        .collect(Collectors.toList());
                predicates.add(root.get("comicsType").in(types));
            }

            // Фільтр за жанрами (використовуємо subquery для пошуку коміксів із заданими жанрами)
            if (genres != null && !genres.isEmpty()) {
                List<Integer> genreIds = Arrays.stream(genres.split(","))
                        .map(String::trim)
                        .map(Integer::parseInt)
                        .collect(Collectors.toList());

                Subquery<Long> genreSubquery = query.subquery(Long.class);
                Root<Comics> subRoot = genreSubquery.correlate(root);
                Join<Comics, Genre> genreJoin = subRoot.join("genres", JoinType.INNER);
                genreSubquery.select(cb.literal(1L))
                        .where(genreJoin.get("id").in(genreIds));

                predicates.add(cb.exists(genreSubquery));
            }

            // Фільтр за кількістю глав – використовуючи розмір списку "chapters"
            if (minChapters != null) {
                Expression<Integer> chapterCount = cb.size(root.get("chapters"));
                predicates.add(cb.ge(chapterCount, minChapters));
            }
            if (maxChapters != null) {
                Expression<Integer> chapterCount = cb.size(root.get("chapters"));
                predicates.add(cb.le(chapterCount, maxChapters));
            }

            if (predicates.isEmpty()) {
                return cb.conjunction();
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return comicsRepository.findAll(spec, pageable);
    }

    // Удосконалений метод сортування
    private Sort getSort(String sortBy) {
        return switch (sortBy.toLowerCase()) {
            case "views" -> Sort.by(Sort.Direction.DESC, "viewCount");
            case "newest" -> Sort.by(Sort.Direction.DESC, "createdAt");
            case "chapters" -> Sort.by(Sort.Direction.DESC, "chapters.size");
            case "title" -> Sort.by(Sort.Direction.ASC, "title");
            default -> Sort.by(Sort.Direction.DESC, "averageRating");
        };
    }


    public List<Comics> findSimilarComics(Comics comic, int limit) {
        return comicsRepository.findAll().stream()
                .filter(Objects::nonNull)
                .map(c -> (Comics) c)
                .filter(c -> c.getId() != comic.getId())
                .filter(c -> !Collections.disjoint(c.getGenres(), comic.getGenres()))
                .filter(c -> c.getComicsType() == comic.getComicsType())
                .sorted(Comparator.comparing(Comics::getPopularityRating, Comparator.reverseOrder()))
                .limit(limit)
                .collect(Collectors.toList());
    }

    // Додайте цей метод до вашого ComicsService
    @Transactional
    public Page<Comics> searchComicsAdvanced(
            String search, String sortBy, int page, int size,
            List<String> genres, int minChapters, ComicsType comicsType) {

        Pageable pageable;
        if ("views".equals(sortBy)) {
            pageable = PageRequest.of(page, size, Sort.by("viewCount").descending());
        } else {
            pageable = PageRequest.of(page, size, Sort.by("averageRating").descending());
        }

        // Використовуємо Specification для побудови складного запиту
        Specification<Comics> spec = Specification.where(null);

        // Додаємо фільтр за назвою, якщо вказаний пошуковий запит
        if (search != null && !search.isEmpty()) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), "%" + search.toLowerCase() + "%"));
        }

        // Додаємо фільтр за типом коміксу
        if (comicsType != null) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("comicsType"), comicsType));
        }

        // Додаємо фільтр за кількістю глав
        if (minChapters > 1) {
            spec = spec.and((root, query, criteriaBuilder) -> {
                // Підзапит для підрахунку кількості глав
                Subquery<Long> chapterCountSubquery = query.subquery(Long.class);
                Root<Chapter> chapterRoot = chapterCountSubquery.from(Chapter.class);
                chapterCountSubquery.select(criteriaBuilder.count(chapterRoot))
                        .where(criteriaBuilder.equal(chapterRoot.get("comics"), root));

                return criteriaBuilder.greaterThanOrEqualTo(chapterCountSubquery, (long) minChapters);
            });
        }

        // Додаємо фільтр за жанрами
        if (genres != null && !genres.isEmpty()) {
            for (String genreName : genres) {
                spec = spec.and((root, query, criteriaBuilder) -> {
                    Join<Comics, Genre> genreJoin = root.join("genres");
                    return criteriaBuilder.equal(criteriaBuilder.lower(genreJoin.get("name")), genreName.toLowerCase());
                });
            }
        }

        return comicsRepository.findAll(spec, pageable);
    }
}
