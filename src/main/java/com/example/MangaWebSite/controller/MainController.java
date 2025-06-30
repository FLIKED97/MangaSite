package com.example.MangaWebSite.controller;

import com.example.MangaWebSite.models.Chapter;
import com.example.MangaWebSite.models.Comics;
import com.example.MangaWebSite.models.ReadingProgress;
import com.example.MangaWebSite.security.PersonDetails;
import com.example.MangaWebSite.service.*;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Controller
@AllArgsConstructor
@RequestMapping("/main")
public class MainController {

    private final PersonService personService;
    private final ComicsService comicsService;

    private final ChapterService chapterService;

    private final ReadingProgressService readingProgressService;

    private final TabsService tabsService;

    private static final Logger logger = LoggerFactory.getLogger(MainController.class); // For SLF4J

    private final GenreBasedRecommendationService recommendationService;


    @GetMapping()
    @Transactional(readOnly = true)
    public String mainPage(Model model, Principal principal) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();



        // Додаткові атрибути тільки для авторизованих користувачів
        if (authentication != null && authentication.getPrincipal() instanceof PersonDetails personDetails) {
            List<ReadingProgress> recentlyRead = readingProgressService.getRecentlyReadComicsWithProgress(personDetails.getPerson().getId());
            Map<Integer, Integer> comicsProgress = readingProgressService.getComicsProgress(personDetails.getPerson().getId());

            model.addAttribute("recentlyReadProgress", recentlyRead);
            model.addAttribute("comicsProgress", comicsProgress);
            model.addAttribute("person", personDetails.getPerson());
        }

        //комікси з новими главами, за останній час.
        model.addAttribute("newComics", chapterService.getAllNewChapters(0));
        //Оновлені комікси з закадок.
        model.addAttribute("bookmarkedComics", chapterService.getNewChaptersInTabs(0));
        model.addAttribute("selectedDays", 365);

        if (authentication != null && authentication.getPrincipal() instanceof PersonDetails personDetails) {
            // Отримуємо userId, наприклад, через custom UserDetails або інший сервіс
            int userId = personDetails.getPerson().getId();
            List<Comics> recommendedComics = recommendationService.getRecommendationsBySimilarGenres(userId);
            Map<Integer, Integer> latestChapters = new HashMap<>();
            for (Comics comic : recommendedComics) {
                latestChapters.put(comic.getId(), chapterService.getLatestChapterTitle(comic.getId()));
            }
            model.addAttribute("recommendedComics", recommendedComics);
            model.addAttribute("latestChapters", latestChapters);

        } else {
            // Комікси, які можна показати без авторизації
            List<Comics> popularComics = comicsService.getPopularComicsWithNewChapters(1.0);
            Map<Integer, Integer> latestChapters = new HashMap<>();
            for (Comics comic : popularComics) {
                latestChapters.put(comic.getId(), chapterService.getLatestChapterTitle(comic.getId()));
            }

            model.addAttribute("popularComicsWithNewChapters", popularComics);
            model.addAttribute("latestChapters", latestChapters);
        }
        return "main";
    }
    @GetMapping("/some-page")
    public String getPage(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof PersonDetails personDetails) {
            List<ReadingProgress> recentlyRead = readingProgressService.getRecentlyReadComicsWithProgress(personDetails.getPerson().getId());
            Map<Integer, Integer> comicsProgress = readingProgressService.getComicsProgress(personDetails.getPerson().getId());

            model.addAttribute("recentlyReadProgress", recentlyRead);
            model.addAttribute("comicsProgress", comicsProgress);
        }
        return "page";
    }
    @GetMapping("/comics/new")
    @ResponseBody
    public ResponseEntity<List<Chapter>> getNewChapters(@RequestParam("page") int page) {
        try {
            Page<Chapter> chaptersPage = chapterService.getAllNewChapters(page);
            return ResponseEntity.ok(chaptersPage.getContent());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @GetMapping("/comics/bookmarked")
    @ResponseBody
    @Transactional(readOnly = true)
    public ResponseEntity<List<Chapter>> getBookmarkedComics(@RequestParam("page") int page) {
        try {
            Page<Chapter> chaptersPage = chapterService.getNewChaptersInTabs(page);
            return ResponseEntity.ok(chaptersPage.getContent());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
