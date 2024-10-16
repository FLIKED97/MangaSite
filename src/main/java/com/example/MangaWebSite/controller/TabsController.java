package com.example.MangaWebSite.controller;

import com.example.MangaWebSite.security.PersonDetails;
import com.example.MangaWebSite.service.TabsService;
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
@RequestMapping("/tabs")
public class TabsController {

    private final TabsService tabsService;

    @GetMapping("/{id}")
    public String showTabs(@PathVariable("id") int id, Model model){
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        PersonDetails personDetails = (PersonDetails) authentication.getPrincipal();

        model.addAttribute("tabs", tabsService.findByPersonId(id));
        return "tabs/show";
    }

}
