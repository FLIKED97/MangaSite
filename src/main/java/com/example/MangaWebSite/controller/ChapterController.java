package com.example.MangaWebSite.controller;

import com.example.MangaWebSite.models.Chapter;
import com.example.MangaWebSite.service.ChapterService;
import com.example.MangaWebSite.service.FileSorterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import java.util.List;


@Controller
@RequestMapping("/chapters")
public class ChapterController {

    private final ChapterService chapterService;

    @Autowired
    public ChapterController(ChapterService chapterService) {
        this.chapterService = chapterService;
    }

    @GetMapping("/add")
    public String addChapter(@RequestParam("comicId") int comicId, Model model){
        model.addAttribute("chapter", new Chapter());
        model.addAttribute("comicId", comicId);
        return "chapter/create";
    }

    @PostMapping("/add")
    public String addNewChapter(@RequestParam int comicId,
                                @RequestParam String title,
                                @RequestParam List<MultipartFile> pages,
                                @RequestParam String fileOrder) throws IOException {

        List<MultipartFile> sortedPages = FileSorterService.sortFilesByOrder(fileOrder, pages);
        chapterService.addChapterWithPages(comicId, title, sortedPages);
        return "redirect:/comics/" + comicId;
    }


}
