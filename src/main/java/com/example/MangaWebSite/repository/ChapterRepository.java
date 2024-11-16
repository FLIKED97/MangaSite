package com.example.MangaWebSite.repository;

import com.example.MangaWebSite.models.Chapter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChapterRepository extends JpaRepository<Chapter, Integer> {
//    @Query("SELECT MAX(c.chapterNumber) FROM Chapter c WHERE c.comics.id = :comicId")
//    Integer findMaxChapterNumberByComicId(@Param("comicId") int comicId);

    @Query("SELECT c FROM Chapter c WHERE c.comics.id = :comicId AND c.chapterNumber = (SELECT MAX(ch.chapterNumber) FROM Chapter ch WHERE ch.comics.id = :comicId)")
    Optional<Chapter> findLatestChapterByComicId(@Param("comicId") int comicId);

    List<Chapter> findAllByComicsId(int comicId);
}
