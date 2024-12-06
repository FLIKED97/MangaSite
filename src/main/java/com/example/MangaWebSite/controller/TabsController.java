package com.example.MangaWebSite.controller;

import com.example.MangaWebSite.models.Comics;
import com.example.MangaWebSite.models.Tabs;
import com.example.MangaWebSite.service.ComicsService;
import com.example.MangaWebSite.service.TabsService;
import lombok.AllArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;


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
    @GetMapping("/create")
    public String formTab(Model model){
        model.addAttribute("tab", new Tabs());
        return "tabs/create";
    }
    @PostMapping("/create")
    public String createTab(@ModelAttribute("tab") Tabs tab) {
        int personId = tabsService.saveWithRedirect(tab);  //TODO Можливо переробити, не дуже подобається варіант
        return "redirect:/profile/" + personId;
    }
    @PostMapping("/delete/{id}")
    @ResponseBody
    public ResponseEntity<?> deleteTab(@PathVariable("id") int id) {
        tabsService.deleteById(id);
        return ResponseEntity.ok("Tab deleted");
    }

}

