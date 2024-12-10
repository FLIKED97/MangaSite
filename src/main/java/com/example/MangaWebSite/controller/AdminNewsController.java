package com.example.MangaWebSite.controller;

import com.example.MangaWebSite.models.Category;
import com.example.MangaWebSite.models.News;
import com.example.MangaWebSite.models.Person;
import com.example.MangaWebSite.security.PersonDetails;
import com.example.MangaWebSite.service.CategoryService;
import com.example.MangaWebSite.service.NewsService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/news")
@PreAuthorize("hasRole('ROLE_ADMIN')")
@RequiredArgsConstructor
public class AdminNewsController {

    private final NewsService newsService;
    private final CategoryService categoryService;

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("news", new News());
        return "news/admin/create";
    }

    @PostMapping("/create")
    public String createNews(@ModelAttribute News news,
                             @AuthenticationPrincipal PersonDetails personDetails,
            @RequestParam int categoryId) {
        news.setAuthor(personDetails.getPerson());

        // Знаходимо категорію за її ID та встановлюємо її у новині
        Category category = categoryService.getCategoryById(categoryId);
        if (category == null) {
            throw new IllegalArgumentException("Категорія не знайдена!");
        }
        news.setCategory(category);
        news.setIsPublished(true);
        newsService.createNews(news);
        return "redirect:/news";
    }
}


