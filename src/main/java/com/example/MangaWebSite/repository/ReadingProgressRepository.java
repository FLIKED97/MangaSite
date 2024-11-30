package com.example.MangaWebSite.repository;

import com.example.MangaWebSite.models.Chapter;
import com.example.MangaWebSite.models.Comics;
import com.example.MangaWebSite.models.CurrentlyReadingDTO;
import com.example.MangaWebSite.models.ReadingProgress;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReadingProgressRepository extends JpaRepository<ReadingProgress, Integer> {
    Optional<ReadingProgress> findByChapter(Chapter chapter);

    @Query("SELECT rp FROM ReadingProgress rp " +
            "JOIN FETCH rp.comics c " +
            "JOIN FETCH rp.chapter ch " +
            "WHERE rp.person.id = :personId " +
            "ORDER BY rp.updatedAt DESC")
    List<ReadingProgress> findRecentlyReadByPersonId(@Param("personId") int personId, Pageable pageable);

    Optional<ReadingProgress> findByPersonIdAndComicsId(int personId, int comicsId);

    @Query("""
    SELECT c 
    FROM ReadingProgress rp 
    JOIN rp.comics c 
    WHERE rp.updatedAt > :cutoffTime 
    GROUP BY c 
    ORDER BY COUNT(rp.person.id) DESC
    """)
    List<Comics> findCurrentlyReading(@Param("cutoffTime") LocalDateTime cutoffTime);
    @Query("""
    SELECT c 
    FROM ReadingProgress rp 
    JOIN rp.comics c 
    WHERE rp.updatedAt > :cutoffTime 
    GROUP BY c
    ORDER BY COUNT(rp.person.id) DESC, c.popularityRating DESC
    """)
    Page<Comics> findCurrentlyPopularReading(@Param("cutoffTime") LocalDateTime cutoffTime, Pageable topFive);
}
