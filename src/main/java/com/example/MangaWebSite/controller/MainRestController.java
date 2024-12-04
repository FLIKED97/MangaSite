package com.example.MangaWebSite.controller;


import com.example.MangaWebSite.models.Comics;
import com.example.MangaWebSite.service.ComicsService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/main")
@AllArgsConstructor
public class MainRestController {
    private final ComicsService comicsService;

    @GetMapping("/data")
    public ResponseEntity<Map<String, List<Comics>>> getComicsData(@RequestParam(value = "days", defaultValue = "31") int days) {
        Map<String, List<Comics>> data = new HashMap<>();

        data.put("newCreatedComics", comicsService.getNewCreatedComics(0, 3, days));
        data.put("currentlyPopularReading", comicsService.getCurrentlyPopularReading(0, 3, days));
        data.put("popularComics", comicsService.getPopularComicsWithNewChapters(1.0)); // або залежно від логіки

        return ResponseEntity.ok(data);
    }
}
