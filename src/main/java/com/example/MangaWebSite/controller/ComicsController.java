package com.example.MangaWebSite.controller;

import com.example.MangaWebSite.models.*;
import com.example.MangaWebSite.repository.ComicsRepository;
import com.example.MangaWebSite.security.PersonDetails;
import com.example.MangaWebSite.service.*;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.security.Principal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

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

    private final CommentService commentService;
    private final FriendshipService friendshipService;

    private static final Logger logger = LoggerFactory.getLogger(ComicsController.class);


    @GetMapping("/create")
    public String createComics(Model model){
        model.addAttribute("genres", genreService.findAll());  // Підтягнути всі жанри
        model.addAttribute("comics", new Comics());
        return "comics/create";
    }

    @GetMapping("/{id}")
    public String getComicById(@PathVariable("id") int comicId,
                               Model model,
                               @RequestParam(defaultValue = "0") int page,
                               @RequestParam(defaultValue = "10") int size,
                               HttpSession session) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        PersonDetails personDetails = (PersonDetails) authentication.getPrincipal();
        Comics comic = comicsService.getComicById(comicId);
        List<Comics> similarComics = comicsService.findSimilarComics(comic, 5);
//        if (comic == null) {
//            return "error/404";
//        }
        if (comic == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Comic not found");
        }
        System.out.println(comic.getId());
        // Унікальний ключ для сесії з прив'язкою до користувача
        String sessionKey = "viewed_comic_" + personDetails.getPerson().getId() + "_" + comicId;

        // Отримуємо час останнього перегляду
        Long lastViewTime = (Long) session.getAttribute(sessionKey);
        long currentTime = System.currentTimeMillis();

        // Інкрементуємо лічильник переглядів не частіше, ніж раз на 5 хвилин
        if (lastViewTime == null || (currentTime - lastViewTime) > 5 * 60 * 1000) {
            comicsService.incrementViewCount(comic);
            session.setAttribute(sessionKey, currentTime);
        }

        Chapter firstChapter = chapterService.getFirstChapter(comicId);
        model.addAttribute("firstChapter", firstChapter);

        model.addAttribute("similarComics", similarComics);
        model.addAttribute("comic", comic);
        model.addAttribute("genres", genreService.findByComicsId(comicId));
        model.addAttribute("tabs", tabsService.findByPersonId(personDetails.getPerson().getId()));

        Pageable pageable = PageRequest.of(page, size, Sort.by("releaseDate").descending());
        Page<Chapter> chapters = chapterService.findChaptersByComicId(comicId, pageable);

        model.addAttribute("chapters", chapters.getContent());
        model.addAttribute("totalPages", chapters.getTotalPages());
        model.addAttribute("currentPage", page);
        model.addAttribute("userRating", ratingService.getUserRating(comicId, personDetails.getPerson().getId()));
        model.addAttribute("comments", commentService.getCommentsByComicsId(comicId));
        model.addAttribute("friends", friendshipService.getFriends(personDetails.getPerson().getId()));
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
                           @RequestParam("genreIds") List<Integer> genreIds,
                           @RequestParam(value = "publishedAt", required = false) LocalDate publishedAt) {

        // Existing image processing logic
        if (file != null && !file.isEmpty()) {
            try {
                comic.setCoverImage(file.getBytes());
                comic.setImageType(file.getContentType());
            } catch (IOException e) {
                // Error handling
            }
        }

        // Set published date if provided
        if (publishedAt != null) {
            comic.setPublishedAt(publishedAt);
        }

        // Save comic and add genres
        Comics savedComic = comicsService.saveComic(comic);
        comicsService.addGenresToComic(savedComic, genreIds);

        return "redirect:/comics/" + comic.getId();
    }

    @PostMapping("/addComicToTab")
    @Transactional
    public String addComicToTab(@RequestParam int comicId, @RequestParam int tabId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        PersonDetails personDetails = (PersonDetails) authentication.getPrincipal();

        Tabs newTab = tabsService.findById(tabId);

        if (newTab.getPerson().getId() == personDetails.getPerson().getId()) {
            Comics comic = comicsService.getComicById(comicId);

            // Видаляємо комікс з усіх поточних закладок користувача
            List<Tabs> userTabs = tabsService.findTabsByPersonId(personDetails.getPerson().getId());
            for (Tabs tab : userTabs) {
                tab.getComics().remove(comic);
                tabsService.save(tab);
            }

            // Додаємо комікс до нової закладки
            newTab.getComics().add(comic);
            tabsService.save(newTab);
        }

        return "redirect:/comics/" + comicId;
    }
    @GetMapping("/newShow")
    public String listComics(@RequestParam(name = "sortBy", required = false, defaultValue = "rating") String sortBy,
                             Model model,
                             @AuthenticationPrincipal PersonDetails user) {
        List<Comics> comics = comicsService.getComicsSortedBy(sortBy);

        model.addAttribute("comics", comics);
        model.addAttribute("sortBy", sortBy);

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


    @GetMapping("/random")
    public String getRandomComic(HttpSession session) {
        // Отримати список усіх коміксів
        List<Integer> comicIds = comicsService.getAllComicIds(); // Метод повинен повертати список ID коміксів

        if (comicIds.isEmpty()) {
            // Якщо немає жодного комікса, перенаправити на сторінку з повідомленням
            return "redirect:/comics/no-comics";
        }

        // Вибрати випадковий ID
        Random random = new Random();
        int randomIndex = random.nextInt(comicIds.size());
        int randomComicId = comicIds.get(randomIndex);

        // Перенаправити на сторінку вибраного комікса
        return "redirect:/comics/" + randomComicId;
    }

    @GetMapping("/bookmarked")
    @ResponseBody
    public List<Integer> getBookmarkedComicsIds(@AuthenticationPrincipal Person person) {
        if (person == null) {
            return Collections.emptyList();
        }
        return tabsService.getBookmarkedComicsIds(person);
    }
}
