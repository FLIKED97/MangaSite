package com.example.MangaWebSite.repository;

import com.example.MangaWebSite.models.Comics;
import com.example.MangaWebSite.models.Genre;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ComicsRepository extends JpaRepository<Comics, Integer> {

    List<Comics> findAllByTabsId(int tabId);

    List<Comics> findAllByOrderByViewCountDesc();

    // Метод для вибору популярних коміксів із новими главами
    @Query("SELECT c FROM Comics c JOIN c.chapters ch WHERE c.popularityRating > :threshold AND ch.releaseDate > :oneMonthAgo")
    List<Comics> findPopularComicsWithNewChapters(@Param("threshold")double threshold, @Param("oneMonthAgo") LocalDate oneMonthAgo);
    List<Comics> findAllComicsByGenres(List<Genre> genres);
}
