package com.example.MangaWebSite.controller;

import com.example.MangaWebSite.models.Friendship;
import com.example.MangaWebSite.models.Person;
import com.example.MangaWebSite.security.PersonDetails;
import com.example.MangaWebSite.service.ComicsService;
import com.example.MangaWebSite.service.FriendshipService;
import com.example.MangaWebSite.service.PersonService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
@RequestMapping("/profile")
@AllArgsConstructor
public class ProfileController {
    private final PersonService personService;
    private final ComicsService comicsService;
    private final FriendshipService friendshipService;

    @GetMapping("/personal/{id}")
    public String showProfile(@PathVariable int id, Model model) {
        // Завантажити користувача з бази за ID
        Person person = personService.findById(id);

        if (person == null) {
            System.out.println("Користувач не знайдений");
        }

        model.addAttribute("person", person);
        return "profile/profile"; // Ім'я HTML-шаблону
    }


    @GetMapping("/{id}")
    public String viewProfile(@PathVariable int id, Model model) {
        Person user = personService.findById(id);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        PersonDetails personDetails = (PersonDetails) authentication.getPrincipal();
        boolean isFriend = friendshipService.areFriends(personDetails.getPerson().getId(), id);

        model.addAttribute("user", user);
        model.addAttribute("comics", comicsService.findComicsByPersonId(id));
        model.addAttribute("isFriend", isFriend);
        return "profile";
    }
    @PostMapping("/{id}/edit")
    public String updateProfile(@PathVariable int id, @ModelAttribute Person updatedPerson) {
        // Логіка оновлення користувача
        personService.updatePerson(id, updatedPerson);
        return "redirect:/profile/personal/" + id; // Повернення до профілю після оновлення
    }
}
