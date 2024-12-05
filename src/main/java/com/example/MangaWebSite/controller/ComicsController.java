package com.example.MangaWebSite.controller;

import com.example.MangaWebSite.models.Comics;
import com.example.MangaWebSite.models.Genre;
import com.example.MangaWebSite.models.Person;
import com.example.MangaWebSite.models.Tabs;
import com.example.MangaWebSite.security.PersonDetails;
import com.example.MangaWebSite.service.*;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/comics")
@AllArgsConstructor
public class ComicsController {

    private final ComicsService comicsService;
    private final GenreService genreService;

    private final TabsService tabsService;
    private final ChapterService chapterService;

    private final ReadingProgressService readingProgressService;

    private final RatingService ratingService;

//    @GetMapping()
//    public String showAllComics(Model model){
//        model.addAttribute("comics", comicsService.showAll());
//        return "comics/show";
//    }

    @GetMapping("/create")
    public String createComics(Model model){
        model.addAttribute("genres", genreService.findAll());  // Підтягнути всі жанри
        model.addAttribute("comics", new Comics());
        return "comics/create";
    }

    @GetMapping("/{id}")
    public String getComicById(@PathVariable("id") int comicId,
                               Model model,
                               HttpSession session) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        PersonDetails personDetails = (PersonDetails) authentication.getPrincipal();
        Comics comic = comicsService.getComicById(comicId);
        if (comic == null) {
            return "error/404";
        }

        // Перевірити, чи комікс вже переглядався у цій сесії
        String sessionKey = "viewed_comic_" + comicId;
        if (session.getAttribute(sessionKey) == null) {
            comicsService.incrementViewCount(comic);
            session.setAttribute(sessionKey, true); // Позначити як переглянутий
        }

        model.addAttribute("comic", comic);
        model.addAttribute("genres", genreService.findByComicsId(comicId));
        model.addAttribute("tabs", tabsService.findByPersonId(personDetails.getPerson().getId()));
        model.addAttribute("chapters", chapterService.findAllChapterByComicsId(comicId));
        model.addAttribute("userRating", ratingService.getUserRating(comicId, personDetails.getPerson().getId()));
        return "comics/comic-details";
    }
    @GetMapping("/image/{id}")
    @ResponseBody
    public ResponseEntity<byte[]> getComicCoverImage(@PathVariable("id") int id) {
        Comics comic = comicsService.getComicById(id);
        if (comic != null && comic.getCoverImage() != null) {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(comic.getImageType()));
            return new ResponseEntity<>(comic.getCoverImage(), headers, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/create")
    public String addComic(@ModelAttribute("comic") Comics comic,
                           @RequestParam("imageByte") MultipartFile file,
                           @RequestParam("genreIds") List<Integer> genreIds) {

        // Обробка зображення (обкладинки)
        if (file != null && !file.isEmpty()) {
            try {
                comic.setCoverImage(file.getBytes()); //TODO Можливо Переробити
                comic.setImageType(file.getContentType());
            } catch (IOException e) {
                System.out.println("Помилка при завантаженні файлу: " + e.getMessage());
            }
        }

        // Спочатку зберігаємо комікс без жанрів
        Comics savedComic = comicsService.saveComic(comic);

        // Після того як комікс збережений, додаємо жанри
        comicsService.addGenresToComic(savedComic, genreIds);

        return "redirect:/comics";
    }

    @PostMapping("/addComicToTab")
    public String addComicToTab(@RequestParam int comicId, @RequestParam int tabId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        PersonDetails personDetails = (PersonDetails) authentication.getPrincipal();

        Tabs tab = tabsService.findById(tabId);

        if (tab.getPerson().getId() == personDetails.getPerson().getId()) {
            Comics comic = comicsService.getComicById(comicId);
            tab.getComics().add(comic);
            tabsService.save(tab); // Зберігаємо зміни в базі
        }

        return "redirect:/comics/" + comicId;
    }

    @GetMapping("/newShow")
    public String listComics(@RequestParam(name = "sortBy", required = false, defaultValue = "rating") String sortBy,
                             Model model,
                             @RequestParam(name = "genres", required = false) List<Integer> genres) {
        List<Comics> comics;
        if (genres != null && !genres.isEmpty()) {
            comics = comicsService.getComicsByGenresAndSort(genres, sortBy); // Фільтрація за жанрами
        } else {
            comics = comicsService.getComicsSortedBy(sortBy); // Без фільтрації
        }

        model.addAttribute("comics", comics);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("allGenres", genreService.findAll());
        model.addAttribute("selectedGenres", genres != null ? genres : new ArrayList<>()); // Для відображення вибраних
        return "comics/newShow";
    }

    @GetMapping("/sections")
    public String getComicsBySection(
            @RequestParam(value = "section", defaultValue = "current") String section,
            @RequestParam(value = "days", defaultValue = "31") int days,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
            Model model) {

        section = validateSection(section); // Перевірка секції

        // Вибір коміксів залежно від секції
        List<Comics> comics = switch (section) {
            case "popular" -> comicsService.getCurrentlyPopularReading(page, pageSize, days);
            case "new" -> comicsService.getNewCreatedComics(page, pageSize, days);
            default -> comicsService.getCurrentlyReading(page, pageSize, days);
        };

        // Передача атрибутів у модель
        model.addAttribute("comics", comics);
        model.addAttribute("section", section);
        model.addAttribute("selectedDays", days);
        return "currently-reading/currently-reading";
    }


    // Метод для перевірки та парсингу числа
    private int parseIntOrDefault(String value, int defaultValue) {
        try {
            return Integer.parseInt(value); // Спроба конвертації
        } catch (NumberFormatException ex) {
            return defaultValue; // Якщо не число — повертаємо значення за замовчуванням
        }
    }

    // Перевірка секції
    private String validateSection(String section) {
        Set<String> validSections = Set.of("current", "popular", "new");
        return validSections.contains(section) ? section : "current";
    }



}
