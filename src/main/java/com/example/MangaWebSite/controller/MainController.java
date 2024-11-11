package com.example.MangaWebSite.controller;

import com.example.MangaWebSite.models.Comics;
import com.example.MangaWebSite.security.PersonDetails;
import com.example.MangaWebSite.service.ComicsService;
import com.example.MangaWebSite.service.PersonService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;



@Controller
@AllArgsConstructor
@RequestMapping("/main")
public class MainController {

    private final PersonService personService;
    private final ComicsService comicsService;

    private static final Logger logger = LoggerFactory.getLogger(MainController.class); // For SLF4J

    @GetMapping()
    public String mainPage(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        PersonDetails personDetails = (PersonDetails) authentication.getPrincipal();

//        // Set a default threshold value or pass it as a parameter
//        double threshold = 1.0; // Adjust the threshold as needed
//
//        try {
//            List<Comics> popularComics = comicsService.getPopularComicsWithNewChapters(threshold);
//            model.addAttribute("popularComicsWithNewChapters", popularComics);
//        } catch (Exception e) {
//            // Handle the exception appropriately, e.g., log the error and display an error message to the user
//            logger.error("Error fetching popular comics:", e);
//            model.addAttribute("errorMessage", "An error occurred while fetching popular comics.");
//        }
        model.addAttribute("popularComicsWithNewChapters", comicsService.getPopularComics()); //TODO ПЕРЕРОБИТИ ЩОБ ВИВОДИЛИСЬ КОМІКСИ З ПОПУЛЯРНИХ І ЩОБ НА ПОЧАТКУ БУЛИ З САМИМИ НОВИМИ ГЛАВАМИ

        model.addAttribute("person", personDetails.getPerson());
        return "main";
    }

}
