package com.example.MangaWebSite.repository;

import com.example.MangaWebSite.models.Comics;
import com.example.MangaWebSite.models.Genre;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ComicsRepository extends JpaRepository<Comics, Integer>, JpaSpecificationExecutor<Comics> {
    List<Comics> findAllByTabsId(int tabId);
    List<Comics> findAllByOrderByViewCountDesc();
    // Метод для вибору популярних коміксів із новими главами
    @Query("SELECT c FROM Comics c JOIN c.chapters ch WHERE c.popularityRating > :threshold AND ch.releaseDate > :oneMonthAgo")
    List<Comics> findPopularComicsWithNewChapters(@Param("threshold")double threshold, @Param("oneMonthAgo") LocalDateTime oneMonthAgo);
    @Query("SELECT c FROM Comics c JOIN c.chapters ch WHERE ch.releaseDate > :oneMonthAgo")
    Page<Comics> findAllComicsWithNewChapters(@Param("oneMonthAgo") LocalDateTime oneMonthAgo, Pageable pageable);
    List<Comics> findAllComicsByGenres(List<Genre> genres);

    @Query("SELECT c FROM Comics c WHERE c.createdAt > :oneMonthAgo")
    Page<Comics> findAllByCreatedAt(Pageable pageable, LocalDateTime oneMonthAgo);
    @Query("SELECT c FROM Comics c WHERE LOWER(c.title) LIKE LOWER(CONCAT('%', :term, '%'))")
    List<Comics> findByTitleContaining(@Param("term") String term);

    @Query("SELECT c FROM Comics c WHERE LOWER(c.author) LIKE LOWER(CONCAT('%', :term, '%'))")
    List<Comics> findByAuthorContaining(@Param("term") String term);

//    @Query("""
//    SELECT c
//    FROM ReadingProgress rp
//    JOIN rp.comics c
//    WHERE rp.updatedAt > :cutoffTime
//    GROUP BY c
//    ORDER BY COUNT(rp.person.id) DESC
//    """)
//    List<Comics> findCurrentlyReading(@Param("cutoffTime") LocalDateTime cutoffTime);

    @Query("SELECT c.id FROM Comics c")
    List<Integer> findAllComicIds();

    @Query("SELECT c FROM Comics c " +
            "JOIN c.tabs t " +
            "WHERE t.person.id = :personId")
    List<Comics> findAllByPersonId(@Param("personId") int personId);

    @Query("SELECT c FROM Comics c LEFT JOIN c.ratings r " +
            "WHERE LOWER(c.title) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "GROUP BY c " +
            "ORDER BY CASE " +
            "WHEN :sortBy = 'rating' THEN COALESCE(AVG(r.rating), 0) " +
            "WHEN :sortBy = 'views' THEN c.viewCount " +
            "ELSE c.popularityRating END DESC")
    Page<Comics> findByTitleContainingIgnoreCaseAndSort(
            @Param("search") String search,
            @Param("sortBy") String sortBy,
            Pageable pageable
    );

//    @Query("SELECT DISTINCT c FROM Comics c LEFT JOIN FETCH c.tabs t " +
//            "WHERE LOWER(c.title) LIKE LOWER(CONCAT('%', :search, '%')) " +
//            "AND (t IS NULL OR t.person.id = :personId) " +
//            "GROUP BY c.id " +
//            "ORDER BY CASE WHEN :sortBy = 'rating' THEN c.popularityRating " +
//            "WHEN :sortBy = 'views' THEN c.viewCount " +
//            "ELSE c.popularityRating END DESC")
//    Page<Comics> findByTitleContainingIgnoreCaseAndSortWithTabs(
//            @Param("search") String search,
//            @Param("sortBy") String sortBy,
//            @Param("personId") int personId,
//            Pageable pageable
//    );
        // Пошук топ-5 коміксів за популярністю, виключаючи список певних ID
        List<Comics> findTop5ByIdNotInOrderByPopularityRatingDesc(List<Integer> excludedIds);

            // Пошук топ-5 унікальних коміксів за схожими жанрами (distinct),
            // виключаючи певні комікси та сортування за показником популярності
        List<Comics> findDistinctTop5ByGenresInAndIdNotInOrderByPopularityRatingDesc(List<Genre> genres, List<Integer> excludedIds);

}
