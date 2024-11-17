package com.example.MangaWebSite.repository;

import com.example.MangaWebSite.models.ComicPage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PageRepository extends JpaRepository<ComicPage, Integer> {
    List<ComicPage> findAllByChapterId(int chapterId);

//    @Query("SELECT p FROM ComicPage p WHERE p.chapter.id = :chapterId")
//    Page<ComicPage> findPagesByChapterId(@Param("chapterId") int chapterId, Pageable pageable);
    Page<ComicPage> findByChapterId(int chapterId, Pageable pageable);


    Page<ComicPage> findByChapterIdOrderByPageNumberAsc(int id, Pageable pageable);
}
