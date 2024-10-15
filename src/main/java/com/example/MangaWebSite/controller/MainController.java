package com.example.MangaWebSite.controller;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@AllArgsConstructor
@RequestMapping("/main")
public class MainController {

    @GetMapping()
    public String mainPage(Model model){


        return "main";
    }
}
