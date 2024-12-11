package com.example.MangaWebSite.controller;

import com.example.MangaWebSite.models.News;
import com.example.MangaWebSite.models.Person;
import com.example.MangaWebSite.security.PersonDetails;
import com.example.MangaWebSite.service.CommentService;
import com.example.MangaWebSite.service.NewsCommentService;
import com.example.MangaWebSite.service.NewsService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/news")
@RequiredArgsConstructor
public class NewsController {

    private final NewsService newsService;
    private final NewsCommentService newsCommentService;

    @GetMapping
    public String listNews(Model model) {
        model.addAttribute("newsList", newsService.getAllPublishedNews());
        return "news/list";
    }

    @GetMapping("/{id}")
    public String viewNews(@PathVariable int id, Model model) {
        News news = newsService.getNewsById(id);
        model.addAttribute("news", news);
        model.addAttribute("comments", newsCommentService.getCommentsForNews(news));
        return "news/view";
    }

    @PostMapping("/{id}/comments/add")
    public String addComment(@PathVariable int id, @AuthenticationPrincipal PersonDetails user, @RequestParam String content) {
        newsCommentService.addComment(id, user.getPerson(), content);
        return "redirect:/news/" + id;
    }
}
