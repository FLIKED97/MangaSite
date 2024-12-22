package com.example.MangaWebSite.repository;

import com.example.MangaWebSite.models.Chapter;
import com.example.MangaWebSite.models.Comics;
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
public interface ChapterRepository extends JpaRepository<Chapter, Integer> {
//    @Query("SELECT MAX(c.chapterNumber) FROM Chapter c WHERE c.comics.id = :comicId")
//    Integer findMaxChapterNumberByComicId(@Param("comicId") int comicId);

    @Query("SELECT c FROM Chapter c WHERE c.comics.id = :comicId AND c.chapterNumber = (SELECT MAX(ch.chapterNumber) FROM Chapter ch WHERE ch.comics.id = :comicId)")
    Optional<Chapter> findLatestChapterByComicId(@Param("comicId") int comicId);

    List<Chapter> findAllByComicsId(int comicId);

    Chapter findFirstByComicsAndChapterNumberGreaterThanOrderByChapterNumberAsc(Comics comics, int chapterNumber);
    @Query("SELECT ch FROM Chapter ch JOIN FETCH ch.comics c WHERE ch.releaseDate > :oneMonthAgo ORDER BY ch.releaseDate DESC")
    Page<Chapter> findAllNewChapters(@Param("oneMonthAgo") LocalDateTime oneMonthAgo, Pageable pageable);

    @Query("SELECT ch FROM Chapter ch " +
            "LEFT JOIN ch.comics c " +
            "JOIN c.tabs t " +
            "WHERE t.person.id = :personId ")
    Page<Chapter> findNewChaptersInTabs(@Param("personId") int personId, Pageable pageable);

}
