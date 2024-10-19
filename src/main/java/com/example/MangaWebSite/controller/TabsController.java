package com.example.MangaWebSite.controller;

import com.example.MangaWebSite.service.ComicsService;
import com.example.MangaWebSite.service.TabsService;
import lombok.AllArgsConstructor;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@AllArgsConstructor
@RequestMapping("/tabs")
public class TabsController {

    private final TabsService tabsService;

    private final ComicsService comicsService;
    @GetMapping("/person/{personId}")
    public String showTabs(@PathVariable("personId") int personId, Model model) {
        model.addAttribute("tabs", tabsService.findByPersonId(personId));
        return "tabs/show";
    }

    @GetMapping("/person/{personId}/tab/{tabId}")
    public String getComicsByTab(@PathVariable("personId") int personId, @PathVariable("tabId") int tabId, Model model) {
        model.addAttribute("comics", comicsService.getComicsByTabId(tabId));
        return "tabs/comicsList :: comics-fragment";  // Повертаємо фрагмент для оновлення через AJAX
    }

}

