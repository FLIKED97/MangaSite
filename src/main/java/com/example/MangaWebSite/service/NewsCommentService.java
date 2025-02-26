package com.example.MangaWebSite.service;

import com.example.MangaWebSite.event.CommentCreatedEvent;
import com.example.MangaWebSite.models.News;
import com.example.MangaWebSite.models.NewsComment;
import com.example.MangaWebSite.models.Person;
import com.example.MangaWebSite.repository.NewsCommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class NewsCommentService {
    private final NewsCommentRepository newsCommentRepository;
    private final NewsService newsService;
    private final ApplicationEventPublisher eventPublisher;



    public List<NewsComment> getCommentsForNews(News news) {
        return newsCommentRepository.findAllByNewsId(news.getId());
    }

    public void addComment(int newsId, com.example.MangaWebSite.models.Person user, String content) {
        NewsComment newsComment = new NewsComment();
        newsComment.setNews(newsService.getNewsById(newsId));
        newsComment.setPerson(user);
        newsComment.setText(content);
        newsComment.setCreatedAt(LocalDateTime.now());
        newsCommentRepository.save(newsComment);

        // Публікуємо ту ж універсальну подію, вказуючи тип "news"
        eventPublisher.publishEvent(new CommentCreatedEvent(this, user, "news"));
    }

    public List<NewsComment> getAllNewsCommentsByPersonId(int personId) {
        return newsCommentRepository.findAllByPersonId(personId);
    }
}
