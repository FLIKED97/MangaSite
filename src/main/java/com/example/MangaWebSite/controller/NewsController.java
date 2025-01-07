package com.example.MangaWebSite.controller;

import com.example.MangaWebSite.models.Category;
import com.example.MangaWebSite.models.News;
import com.example.MangaWebSite.models.Person;
import com.example.MangaWebSite.security.PersonDetails;
import com.example.MangaWebSite.service.CategoryService;
import com.example.MangaWebSite.service.CommentService;
import com.example.MangaWebSite.service.NewsCommentService;
import com.example.MangaWebSite.service.NewsService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/news")
@RequiredArgsConstructor
public class NewsController {

    private final NewsService newsService;
    private final NewsCommentService newsCommentService;
    private final CategoryService categoryService;

    @GetMapping
    public String listNews(@RequestParam(required = false) Integer category, Model model) {
        List<News> newsList;
        Category selectedCategory = null;

        if (category != null) {
            selectedCategory = categoryService.getCategoryById(category);
            newsList = newsService.findByCategory(selectedCategory);
        } else {
            newsList = newsService.findAll();
        }

        model.addAttribute("newsList", newsList);
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("selectedCategory", selectedCategory);

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
