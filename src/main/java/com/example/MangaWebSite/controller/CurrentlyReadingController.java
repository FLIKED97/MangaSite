package com.example.MangaWebSite.controller;

import com.example.MangaWebSite.service.ReadingProgressService;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/currently-reading")
@AllArgsConstructor
public class CurrentlyReadingController {
    private final ReadingProgressService readingProgressService;

    @GetMapping()
    public String  getCurrentlyReading(Model model){
        model.addAttribute("currentlyReading", readingProgressService.getCurrentlyReading());
        return"currently-reading/currently-reading";
    }
}
