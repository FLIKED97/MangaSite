package com.example.MangaWebSite.controller;

import com.example.MangaWebSite.models.Comics;
import com.example.MangaWebSite.models.Person;
import com.example.MangaWebSite.models.Publisher;
import com.example.MangaWebSite.service.ComicsService;
import com.example.MangaWebSite.service.PersonService;
import com.example.MangaWebSite.service.PublisherService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("/search")
@AllArgsConstructor
public class SearchController {
    private final ComicsService comicsService;
    private final PersonService personService;
    private final PublisherService publisherService;

    @GetMapping
    public String searchPage() {
        return "search"; // Рендер сторінки з модальним пошуком
    }

    @GetMapping("/comics")
    @ResponseBody
    public List<Comics> searchComics(@RequestParam("term") String term) {
        return comicsService.searchByTitle(term);
    }

    @GetMapping("/authors")
    @ResponseBody
    public List<Comics> searchAuthors(@RequestParam("term") String term) {
        return comicsService.searchByAuthor(term);
    }

    @GetMapping("/users")
    @ResponseBody
    public List<Person> searchUsers(@RequestParam("term") String term) {
        return personService.searchByUsername(term);
    }

    @GetMapping("/groups")
    @ResponseBody
    public List<Publisher> searchGroups(@RequestParam("term") String term) {
        return publisherService.searchByName(term);
    }
}

