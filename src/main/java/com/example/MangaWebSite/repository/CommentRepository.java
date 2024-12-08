package com.example.MangaWebSite.repository;

import com.example.MangaWebSite.models.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Integer> {
    List<Comment> findAllByComicsId(int comicsId);

    List<Comment> findAllByComicsIdOrderByCreatedAtDesc(int comicsId);

    List<Comment> findAllByPersonId(int id);
}
