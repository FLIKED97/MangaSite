package com.example.MangaWebSite.controller;

import com.example.MangaWebSite.models.Person;
import com.example.MangaWebSite.service.PersonService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/profile")
@AllArgsConstructor
public class ProfileController {
    private final PersonService personService;

    @GetMapping("/{id}")
    public String showProfile(@PathVariable int id, Model model) {
        // Завантажити користувача з бази за ID
        Person person = personService.findById(id);

        if (person == null) {
            System.out.println("Користувач не знайдений");
        }

        model.addAttribute("person", person);
        return "profile/profile"; // Ім'я HTML-шаблону
    }

    @PostMapping("/{id}/edit")
    public String updateProfile(@PathVariable int id, @ModelAttribute Person updatedPerson) {
        // Логіка оновлення користувача
        personService.updatePerson(id, updatedPerson);
        return "redirect:/profile/" + id; // Повернення до профілю після оновлення
    }
}
