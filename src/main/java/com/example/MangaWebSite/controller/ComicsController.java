package com.example.MangaWebSite.controller;

import com.example.MangaWebSite.models.Comics;
import com.example.MangaWebSite.models.Genre;
import com.example.MangaWebSite.service.ComicsService;
import com.example.MangaWebSite.service.GenreService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/comics")
@AllArgsConstructor
public class ComicsController {

    private final ComicsService comicsService;
    private final GenreService genreService;

    @GetMapping()
    public String showAllComics(Model model){
        model.addAttribute("comics", comicsService.showAll());
        return "comics/show";
    }

    @GetMapping("/create")
    public String createComics(Model model){
        model.addAttribute("genres", genreService.findAll());  // Підтягнути всі жанри
        model.addAttribute("comics", new Comics());
        return "comics/create";
    }

    @GetMapping("/{id}")
    public String getComicById(@PathVariable("id") int id, Model model) {
        Comics comic = comicsService.getComicById(id);
        if (comic == null) {
            return "error/404";
        }

        model.addAttribute("comic", comic);
        model.addAttribute("genres", genreService.findByComicsId(id));
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



}
