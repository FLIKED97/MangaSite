package com.example.MangaWebSite.controller;

import com.example.MangaWebSite.models.Chapter;
import com.example.MangaWebSite.service.ChapterService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@Controller
@RequestMapping("/chapters")
public class ChapterController {
    private final ChapterService chapterService;

    @GetMapping("/add")
    public String addChapter(@RequestParam("comicId") int comicId, Model model){
        model.addAttribute("chapter", new Chapter());
        model.addAttribute("comicId", comicId);
        return "chapter/create";
    }

    @PostMapping("/add")
    public String addNewChapter(@RequestParam("comicId") int comicId, @ModelAttribute("chapter") Chapter chapter) {
        chapterService.addChapter(comicId, chapter);
        return "redirect:/main";
    }

}
