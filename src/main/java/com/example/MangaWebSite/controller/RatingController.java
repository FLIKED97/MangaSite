package com.example.MangaWebSite.controller;

import com.example.MangaWebSite.models.Rating;
import com.example.MangaWebSite.security.PersonDetails;
import com.example.MangaWebSite.service.RatingService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@AllArgsConstructor
@RequestMapping("/ratings")
public class RatingController {

    private final RatingService ratingService;


    @PostMapping("/add")
    @ResponseBody
    public ResponseEntity<?> addRating(@RequestBody Map<String, Object> ratingRequest) {
        // Отримуємо comicId і rating з JSON
        int comicId = (Integer) ratingRequest.get("comicId");
        int rating = (Integer) ratingRequest.get("rating");

        System.out.println("Comic ID: " + comicId);

        try {
            ratingService.saveOrUpdateComicsRating(comicId, rating);
            return ResponseEntity.ok("Rating saved successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error saving rating");
        }
    }

}
