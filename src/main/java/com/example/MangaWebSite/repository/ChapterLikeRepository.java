package com.example.MangaWebSite.repository;

import com.example.MangaWebSite.models.Chapter;
import com.example.MangaWebSite.models.ChapterLike;
import com.example.MangaWebSite.models.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChapterLikeRepository extends JpaRepository<ChapterLike, Integer> {
    long countByChapter(Chapter chapter);

    ChapterLike findByChapterIdAndPersonId(int chapterId, int id);
}

