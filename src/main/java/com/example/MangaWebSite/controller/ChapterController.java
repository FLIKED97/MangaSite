package com.example.MangaWebSite.controller;

import com.example.MangaWebSite.models.Chapter;
import com.example.MangaWebSite.models.ChapterLike;
import com.example.MangaWebSite.models.ComicPage;
import com.example.MangaWebSite.models.ReadingProgress;
import com.example.MangaWebSite.repository.ChapterLikeRepository;
import com.example.MangaWebSite.security.PersonDetails;
import com.example.MangaWebSite.service.*;
import lombok.AllArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
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

    private final ReadingProgressService readingProgressService;
    private final ChapterLikeService chapterLikeService;


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
    public String showChapter(@PathVariable int id, Model model,
                              @AuthenticationPrincipal PersonDetails user) {
        Chapter chapter = chapterService.findById(id);
        Chapter nextChapter = chapterService.findNextChapter(chapter);
        Chapter prevChapter = chapterService.findPrevChapter(chapter);

        // Отримуємо id коміксу з поточної глави
        int comicsId = chapter.getComics().getId();

        model.addAttribute("chapter", chapter);
        model.addAttribute("nextChapter", nextChapter);
        model.addAttribute("prevChapter", prevChapter);
        model.addAttribute("chapterLike", chapterLikeService.findLikeChapter(id, user.getPerson()).orElse(null));
        long likesCount = chapterLikeService.countLikes(chapter.getId());
        model.addAttribute("likesCount", likesCount);

        List<Chapter> allChapters = chapterService.findAllChapterByComicsId(comicsId);
        System.out.println("Кількість розділів: " + allChapters.size());
        model.addAttribute("allChapter", allChapters);

        ReadingProgress progress = readingProgressService.findByPersonAndChapter(chapter);
        if (progress == null) {
            System.out.println("Прогрес не знайдено для глави: " + chapter.getId());
            // Наприклад, починати з першої сторінки:
            // lastPage = 1;
        } else {
            System.out.println("Прогрес знайдено: " + progress.getLastPage());
        }


        model.addAttribute("lastPage", progress != null ? progress.getLastPage() : 0);

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
    @PostMapping("/{id}/progress")
    @ResponseBody
    public ResponseEntity<?> updateProgress(
            @PathVariable int id,
            @RequestParam int lastPage) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        PersonDetails personDetails = (PersonDetails) authentication.getPrincipal();
        System.out.println(lastPage);

        try {
            Chapter chapter = chapterService.findById(id);
            readingProgressService.saveOrUpdateProgress(personDetails.getPerson(), chapter, lastPage);
            return ResponseEntity.ok("Progress updated");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating progress");
        }
    }

    @GetMapping("/comic/{comicId}")
    @ResponseBody
    @Transactional
    public Page<Chapter> getChaptersByComicId(
            @PathVariable int comicId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "2") int size, Model model) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("releaseDate").descending());
        Page<Chapter> chapters = chapterService.findChaptersByComicId(comicId, pageable);

        // Force initialization of associated Comic if needed
        chapters.forEach(chapter -> Hibernate.initialize(chapter.getComics()));
        return chapters;
    }

    @GetMapping("/comics/{comicId}")
    public String getComicDetails(@PathVariable int comicId,
                                  @RequestParam(defaultValue = "0") int page,
                                  @RequestParam(defaultValue = "2") int size,
                                  Model model) {


        // Інші атрибути, які вам потрібні
        return "comics/comic-details";
    }
}
