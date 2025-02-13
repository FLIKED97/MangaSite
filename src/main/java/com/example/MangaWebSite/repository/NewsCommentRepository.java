package com.example.MangaWebSite.repository;

import com.example.MangaWebSite.models.NewsComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NewsCommentRepository extends JpaRepository<NewsComment, Integer> {
    List<NewsComment> findAllByNewsId(int id);

    List<NewsComment> findAllByPersonId(int personId);
}
