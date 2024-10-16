package com.example.MangaWebSite.controller;

import com.example.MangaWebSite.repository.TabsRepository;
import com.example.MangaWebSite.security.PersonDetails;
import com.example.MangaWebSite.service.PersonService;
import com.example.MangaWebSite.service.TabsService;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@AllArgsConstructor
@RequestMapping("/main")
public class MainController {

    private final PersonService personService;

    @GetMapping()
    public String mainPage(Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        PersonDetails personDetails = (PersonDetails) authentication.getPrincipal();

        model.addAttribute("person", personDetails.getPerson());
        return "main";
    }

}
