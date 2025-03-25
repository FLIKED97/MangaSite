package com.example.MangaWebSite.controller;

import com.example.MangaWebSite.models.Comics;
import com.example.MangaWebSite.service.GenreBasedRecommendationService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/recommendations")
@AllArgsConstructor
public class RecommendationController {

    private GenreBasedRecommendationService recommendationService;


    @GetMapping("/genre")
    public ResponseEntity<List<Comics>> getGenreBasedRecommendations(@RequestParam int userId) {
        List<Comics> recommendations = recommendationService.getRecommendationsBySimilarGenres(userId);
        return ResponseEntity.ok(recommendations);
    }
}

