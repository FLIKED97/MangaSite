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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
    public String listNews(@RequestParam(required = false) Integer category,
                           @RequestParam(defaultValue = "0") int page,
                           Model model,
                           @AuthenticationPrincipal PersonDetails user) {

        Pageable pageable = PageRequest.of(page, 10);
        Page<News> newsPage;
        Category selectedCategory = null;

        if (category != null) {
            selectedCategory = categoryService.getCategoryById(category);
            newsPage = newsService.findByCategory(selectedCategory, pageable);
        } else {
            newsPage = newsService.findAll(pageable);
        }

        boolean isAdmin = user.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));

        model.addAttribute("newsList", newsPage.getContent());
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("selectedCategory", selectedCategory);
        model.addAttribute("isAdmin", isAdmin);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", newsPage.getTotalPages());

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
