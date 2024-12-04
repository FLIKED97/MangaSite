package com.example.MangaWebSite.controller;

import com.example.MangaWebSite.models.Comics;
import com.example.MangaWebSite.service.ComicsService;
import com.example.MangaWebSite.service.ReadingProgressService;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping()
@AllArgsConstructor
public class CurrentlyReadingController {
    private final ReadingProgressService readingProgressService;
    private final ComicsService comicsService;

}
