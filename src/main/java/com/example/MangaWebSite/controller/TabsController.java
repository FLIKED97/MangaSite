package com.example.MangaWebSite.controller;

import com.example.MangaWebSite.models.Comics;
import com.example.MangaWebSite.models.Tabs;
import com.example.MangaWebSite.service.ChapterService;
import com.example.MangaWebSite.service.ComicsService;
import com.example.MangaWebSite.service.TabsService;
import lombok.AllArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Controller
@AllArgsConstructor
@RequestMapping("/tabs")
public class TabsController {

    private final TabsService tabsService;
    private final ComicsService comicsService;
    private final ChapterService chapterService;
    @GetMapping("/person/{personId}")
    @Transactional(readOnly = true)
    public String showTabs(@PathVariable("personId") int personId, Model model) {
        List<Tabs> tabs = tabsService.getTabsWithComicsCount(personId);
        model.addAttribute("tabs", tabs);
        model.addAttribute("personId", personId);
//        model.addAttribute("tabs", tabsService.findByPersonId(personId));
        return "tabs/show";
    }

    @GetMapping("/person/{personId}/tab/{tabId}")
    public String getComicsByTab(@PathVariable("personId") int personId, @PathVariable("tabId") int tabId, Model model) {
        List<Comics> comicsInTabs =  comicsService.getComicsByTabId(tabId);
        model.addAttribute("comics", comicsInTabs);
        Map<Integer, String> latestChapters = new HashMap<>();
        for (Comics comic : comicsInTabs) {
            latestChapters.put(comic.getId(), chapterService.getLatestChapterTitle(comic.getId()));
        }
        model.addAttribute("latestChapters", latestChapters);
        Map<Integer, Integer> totalChapters = new HashMap<>();
        for (Comics comic : comicsInTabs){
            totalChapters.put(comic.getId(), chapterService.findAllChapterByComicsId(comic.getId()).size());
        }
        model.addAttribute("totalChapter", totalChapters);
        model.addAttribute("firstChapter", 1);
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
        return "redirect:/profile/personal/" + personId;
    }
    @PostMapping("/delete/{id}")
    @ResponseBody
    public ResponseEntity<?> deleteTab(@PathVariable("id") int id) {
        tabsService.deleteById(id);
        return ResponseEntity.ok("Tab deleted");
    }

}

