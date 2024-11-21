package com.example.MangaWebSite.controller;

import com.example.MangaWebSite.models.Comics;
import com.example.MangaWebSite.models.Genre;
import com.example.MangaWebSite.models.Person;
import com.example.MangaWebSite.models.Tabs;
import com.example.MangaWebSite.security.PersonDetails;
import com.example.MangaWebSite.service.ChapterService;
import com.example.MangaWebSite.service.ComicsService;
import com.example.MangaWebSite.service.GenreService;
import com.example.MangaWebSite.service.TabsService;
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

@Controller
@RequestMapping("/comics")
@AllArgsConstructor
public class ComicsController {

    private final ComicsService comicsService;
    private final GenreService genreService;

    private final TabsService tabsService;
    private final ChapterService chapterService;

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
    public String getComicById(@PathVariable("id") int comicId, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        PersonDetails personDetails = (PersonDetails) authentication.getPrincipal();
        Comics comic = comicsService.getComicById(comicId);
        if (comic == null) {
            return "error/404";
        }

        model.addAttribute("comic", comic);
        model.addAttribute("genres", genreService.findByComicsId(comicId));
        model.addAttribute("tabs", tabsService.findByPersonId(personDetails.getPerson().getId()));
        model.addAttribute("chapters", chapterService.findAllChapterByComicsId(comicId));
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


}
