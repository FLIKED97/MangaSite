package com.example.MangaWebSite.controller;

import com.example.MangaWebSite.models.Comics;
import com.example.MangaWebSite.service.GenreBasedRecommendationService;
import com.example.MangaWebSite.service.RecommendationService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/recommendations")
@AllArgsConstructor
public class RecommendationController {

    private GenreBasedRecommendationService recommendationService;

    private final RecommendationService recommendationServ;

    @GetMapping("/genre")
    public ResponseEntity<List<Comics>> getGenreBasedRecommendations(@RequestParam int userId) {
        List<Comics> recommendations = recommendationService.getRecommendationsBySimilarGenres(userId);
        return ResponseEntity.ok(recommendations);
    }
    @PostMapping("/recommend")
    public List<Comics> getRecommendations(@RequestBody Map<String, String> payload) throws Exception {
        String query = payload.get("query");
        return recommendationServ.findSimilarComics(query, 1);
    }
}

