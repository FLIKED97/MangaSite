package com.example.MangaWebSite.controller;

import com.example.MangaWebSite.models.Comics;
import com.example.MangaWebSite.models.Tabs;
import com.example.MangaWebSite.security.PersonDetails;
import com.example.MangaWebSite.service.ComicsService;
import com.example.MangaWebSite.service.TabsService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@AllArgsConstructor
@RequestMapping("/tabs")
public class TabsController {

    private final TabsService tabsService;

    private final ComicsService comicsService;
    @GetMapping("/person/{personId}")
    public String showTabs(@PathVariable("personId") int personId, Model model) {
        List<Tabs> tabs = tabsService.findByPersonId(personId);
        model.addAttribute("tabs", tabs);
        return "tabs/show";
    }

    @GetMapping("/person/{personId}/tab/{tabId}")
    public String getComicsByTab(@PathVariable("personId") int personId, @PathVariable("tabId") int tabId, Model model) {
        Tabs tab = tabsService.findById(tabId);

        List<Comics> comics = comicsService.getComicsByTabId(tabId);
        System.out.println(comics.get(0).getTitle());
        model.addAttribute("comics", comics);
        return "tabs/comicsList :: comics-fragment";  // Повертаємо фрагмент для оновлення через AJAX
    }

}

