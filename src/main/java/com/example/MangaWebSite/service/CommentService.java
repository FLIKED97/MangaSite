package com.example.MangaWebSite.service;

import com.example.MangaWebSite.models.Comics;
import com.example.MangaWebSite.models.Comment;
import com.example.MangaWebSite.repository.ComicsRepository;
import com.example.MangaWebSite.repository.CommentRepository;
import com.example.MangaWebSite.security.PersonDetails;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
@AllArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final ComicsRepository comicsRepository;

    public void createCommentInComics(int comicsId, String textComment) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        PersonDetails personDetails = (PersonDetails) authentication.getPrincipal();
        Comment comment = new Comment();
        comment.setComics(comicsRepository.findById(comicsId).orElse(null));
        comment.setCreatedAt(LocalDateTime.now());
        comment.setPerson(personDetails.getPerson());
        comment.setText(textComment);
        commentRepository.save(comment);
    }

    public List<Comment> getCommentsByComicsId(int comicsId) {
        return commentRepository.findAllByComicsIdOrderByCreatedAtDesc(comicsId);
    }
}
