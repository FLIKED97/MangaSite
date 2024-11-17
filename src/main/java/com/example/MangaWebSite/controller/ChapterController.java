package com.example.MangaWebSite.controller;

import com.example.MangaWebSite.models.Chapter;
import com.example.MangaWebSite.models.ComicPage;
import com.example.MangaWebSite.service.ChapterService;
import com.example.MangaWebSite.service.FileSorterService;
import com.example.MangaWebSite.service.PageService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Controller
@RequestMapping("/chapters")
@AllArgsConstructor
public class ChapterController {

    private final ChapterService chapterService;

    private final PageService pageService;


    @GetMapping("/add")
    public String addChapter(@RequestParam("comicId") int comicId, Model model){
        model.addAttribute("chapter", new Chapter());
        model.addAttribute("comicId", comicId);
        return "chapter/create";
    }

    @PostMapping("/add")
    public String addNewChapter(@RequestParam int comicId,
                                @RequestParam String title,
                                @RequestParam List<MultipartFile> comicPages,
                                @RequestParam String fileOrder) throws IOException {

        List<MultipartFile> sortedPages = FileSorterService.sortFilesByOrder(fileOrder, comicPages);
        chapterService.addChapterWithPages(comicId, title, sortedPages);
        return "redirect:/comics/" + comicId;
    }

    @GetMapping("/{id}")
    public String showChapter(@PathVariable int id, Model model) {
        Chapter chapter = chapterService.findById(id);
        Chapter nextChapter = chapterService.findNextChapter(chapter);

        model.addAttribute("chapter", chapter);
        model.addAttribute("nextChapter", nextChapter);

        return "chapter/show";
    }

    @GetMapping("/{id}/pages")
    @ResponseBody
    public ResponseEntity<List<Map<String, Object>>> getChapterPages(
            @PathVariable int id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by("pageNumber").ascending());
            Page<ComicPage> pages = pageService.findByChapterId(id, pageable);

            List<Map<String, Object>> result = pages.getContent().stream()
                    .map(p -> {
                        Map<String, Object> pageMap = new HashMap<>();
                        pageMap.put("id", p.getId());
                        // Конвертуємо локальний шлях у веб-шлях
                        String webPath = p.getImagePath()
                                .replace("C:\\uploads", "/images")
                                .replace("\\", "/");
                        pageMap.put("imagePath", webPath);
                        pageMap.put("pageNumber", p.getPageNumber());
                        return pageMap;
                    })
                    .collect(Collectors.toList());

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }




}
