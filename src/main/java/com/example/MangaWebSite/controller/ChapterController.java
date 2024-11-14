package com.example.MangaWebSite.controller;

import com.example.MangaWebSite.models.Chapter;
import com.example.MangaWebSite.models.Page;
import com.example.MangaWebSite.service.ChapterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/chapters")
public class ChapterController {
    @Value("${upload.path}")  // Шлях для збереження завантажених файлів
    private String uploadDir;
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
                                @RequestParam List<MultipartFile> pages) throws IOException {

        // Додаємо сторінки до глави і зберігаємо главу
        chapterService.addChapterWithPages(comicId, title, pages);
        return "redirect:/comics/" + comicId;
    }
}
