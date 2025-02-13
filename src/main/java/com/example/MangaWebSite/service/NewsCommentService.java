package com.example.MangaWebSite.service;

import com.example.MangaWebSite.models.News;
import com.example.MangaWebSite.models.NewsComment;
import com.example.MangaWebSite.models.Person;
import com.example.MangaWebSite.repository.NewsCommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class NewsCommentService {
    private final NewsCommentRepository newsCommentRepository;
    private final NewsService newsService;


    public List<NewsComment> getCommentsForNews(News news) {
        return newsCommentRepository.findAllByNewsId(news.getId());
    }

    public void addComment(int newsId, Person user, String content) {
        NewsComment newsComment = new NewsComment();
        newsComment.setNews(newsService.getNewsById(newsId));
        newsComment.setPerson(user);
        newsComment.setText(content);
        newsCommentRepository.save(newsComment);
    }

    public List<NewsComment> getAllNewsCommentsByPersonId(int personId) {
        return newsCommentRepository.findAllByPersonId(personId);
    }
}
