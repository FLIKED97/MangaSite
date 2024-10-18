package com.example.MangaWebSite.controller;

import com.example.MangaWebSite.models.Comics;
import com.example.MangaWebSite.models.Genre;
import com.example.MangaWebSite.service.ComicsService;
import com.example.MangaWebSite.service.GenreService;
import lombok.AllArgsConstructor;
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


    @PostMapping("/create")
    public String addComic(@ModelAttribute("comic") Comics comic,
                           @RequestParam("imageByte") MultipartFile file,
                           @RequestParam("genreIds") List<Integer> genreIds) {

        // Обробка зображення (обкладинки)
        if (file != null && !file.isEmpty()) {
            try {
                comic.setCoverImage(file.getBytes()); //TODO Переробити
            } catch (IOException e) {
                System.out.println("Помилка при завантаженні файлу: " + e.getMessage());
            }
        }

        // Спочатку зберігаємо комікс без жанрів
        Comics savedComic = comicsService.saveComic(comic);  // Повертаємо збережений комікс з ID

        // Після того як комікс збережений, додаємо жанри
        comicsService.addGenresToComic(savedComic, genreIds);

        return "redirect:/comics";  // Повертаємо на сторінку з коміксами після успішного додавання
    }



}
