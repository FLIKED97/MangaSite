package com.example.MangaWebSite.controller;


import com.example.MangaWebSite.models.Comics;
import com.example.MangaWebSite.models.ComicsDTO;
import com.example.MangaWebSite.service.ComicsService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/main")
@AllArgsConstructor
public class MainRestController {
    private final ComicsService comicsService;

    @GetMapping("/data")
    public ResponseEntity<Map<String, List<ComicsDTO>>> getComicsData(@RequestParam(value = "days", defaultValue = "31") int days) {
        Map<String, List<ComicsDTO>> data = new HashMap<>();

        // Convert entities to DTOs that don't include the problematic LOB data
        data.put("newCreatedComics", comicsService.getNewCreatedComics(0, 3, days).stream()
                .map(this::convertToDTO).collect(Collectors.toList()));
        data.put("currentlyPopularReading", comicsService.getCurrentlyPopularReading(0, 3, days).stream()
                .map(this::convertToDTO).collect(Collectors.toList()));
        data.put("popularComics", comicsService.getPopularComicsWithNewChapters(1.0).stream()
                .map(this::convertToDTO).collect(Collectors.toList()));

        return ResponseEntity.ok(data);
    }

    private ComicsDTO convertToDTO(Comics comics) {
        ComicsDTO dto = new ComicsDTO();
        dto.setId(comics.getId());
        dto.setTitle(comics.getTitle());
        dto.setComicsTypeDisplay(comics.getComicsType().getDisplayName());
        // Don't include the image data in the DTO
        return dto;
    }
}
