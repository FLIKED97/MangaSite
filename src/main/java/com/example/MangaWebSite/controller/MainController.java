package com.example.MangaWebSite.controller;

import com.example.MangaWebSite.models.Comics;
import com.example.MangaWebSite.models.ReadingProgress;
import com.example.MangaWebSite.security.PersonDetails;
import com.example.MangaWebSite.service.ChapterService;
import com.example.MangaWebSite.service.ComicsService;
import com.example.MangaWebSite.service.PersonService;
import com.example.MangaWebSite.service.ReadingProgressService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

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

    private static final Logger logger = LoggerFactory.getLogger(MainController.class); // For SLF4J

    @GetMapping()
    public String mainPage(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        PersonDetails personDetails = (PersonDetails) authentication.getPrincipal();

//        try {
            List<Comics> popularComics = comicsService.getPopularComicsWithNewChapters(1.0);
            Map<Integer, String> latestChapters = new HashMap<>();
            for (Comics comic : popularComics) {
                latestChapters.put(comic.getId(), chapterService.getLatestChapterTitle(comic.getId()));
            }
//            logger.info("Fetched popular comics: " + popularComics.size() + " items");
//            for (Comics comic : popularComics) {
//                logger.info("Comic: " + comic.getTitle() + ", Popularity Rating: " + comic.getPopularityRating());
//            }
            model.addAttribute("popularComicsWithNewChapters", popularComics);
            model.addAttribute("latestChapters", latestChapters);
            model.addAttribute("recentlyRead", comicsService.getRecentlyRead(personDetails.getPerson()));

//        } catch (Exception e) {
//            logger.error("Error fetching popular comics:", e);
//            model.addAttribute("errorMessage", "An error occurred while fetching popular comics.");
//        }

        List<ReadingProgress> readingProgresses = readingProgressService.getRecentlyReadComicsWithProgress(personDetails.getPerson().getId());
        model.addAttribute("recentlyRead", readingProgresses);


        model.addAttribute("person", personDetails.getPerson());

        //Нові комікси
        model.addAttribute("newComics", comicsService.getNewCreatedComics(0).getContent());
        return "main";
    }
    @GetMapping("/comics/new")
    @ResponseBody
    public ResponseEntity<List<Comics>> getNewComics(@RequestParam("page") int page) {
        try {
            Page<Comics> comicsPage = comicsService.getNewCreatedComics(page);
            return ResponseEntity.ok(comicsPage.getContent());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
