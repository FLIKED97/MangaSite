package com.example.MangaWebSite.controller;


import com.example.MangaWebSite.models.Person;
import com.example.MangaWebSite.service.RegistrationService;
import com.example.MangaWebSite.service.UserProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private final RegistrationService registrationService;
    private final UserProfileService userProfileService;

    @GetMapping("/login")
    public String loginPage(@ModelAttribute("person") Person person){
        return "auth/login";
    }
    @GetMapping("/registration")
    public String showRegistrationForm(Model model) {
        model.addAttribute("person", new Person()); // Додаємо порожній об'єкт
        return "auth/registration";
    }

    @PostMapping(
            value = "/registration",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public String registerUser(
            @ModelAttribute("person") @Valid Person person,
            BindingResult bindingResult,
            @RequestParam("confirmPassword") String confirmPassword,
            @RequestPart(value = "avatar", required = false) MultipartFile avatar,
            Model model
    ) {
        // 1) Переконаємося, що дані від Thymeleaf прийшли
        if (bindingResult == null || bindingResult.hasFieldErrors()) {
            // Тут помилки валідації — одразу повертаємо форму
            return "auth/registration";
        }

        // 2) Перевірка паролів
        if (!person.getPassword().equals(confirmPassword)) {
            model.addAttribute("confirmPasswordError", "Паролі не співпадають");
            return "auth/registration";
        }

        // 3) Обробка аватарки (якщо є)
        if (avatar != null && !avatar.isEmpty()) {
            // saveAvatar(avatar, person);
        }

        // 4) Реєстрація
        registrationService.register(person);
        userProfileService.getUserProfile(person.getId());
        return "redirect:/auth/login";
    }


}
