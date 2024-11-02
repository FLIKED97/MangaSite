package com.example.MangaWebSite.repository;

import com.example.MangaWebSite.models.Chapter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ChapterRepository extends JpaRepository<Chapter, Integer> {
    @Query("SELECT MAX(c.chapterNumber) FROM Chapter c WHERE c.comics.id = :comicId")
    Integer findMaxChapterNumberByComicId(@Param("comicId") int comicId);
}
