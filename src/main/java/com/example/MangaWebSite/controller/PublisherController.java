package com.example.MangaWebSite.controller;

import com.example.MangaWebSite.models.Publisher;
import com.example.MangaWebSite.security.PersonDetails;
import com.example.MangaWebSite.service.PublisherService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/publisher")
@AllArgsConstructor
public class PublisherController {
    private final PublisherService publisherService;

    @GetMapping("/register")
    public String showPublisherRegistrationForm(Model model){
        model.addAttribute("publisher", new Publisher());
        return "publisher/register";
    }

    @PostMapping("/register")
    public String registerPublisher(@ModelAttribute("publisher") Publisher publisher) {
        publisherService.register(publisher);
        return "redirect:/main";
    }

}
