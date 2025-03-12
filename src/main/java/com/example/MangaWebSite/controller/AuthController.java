package com.example.MangaWebSite.controller;


import com.example.MangaWebSite.models.Person;
import com.example.MangaWebSite.service.RegistrationService;
import com.example.MangaWebSite.service.UserProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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
            model.addAttribute("person", new Person());  // Create empty Person object for form binding
            return "auth/registration";  // Return the registration.html template name
        }

    @PostMapping("/registration")
    public String registerUser(@ModelAttribute("person") @Valid Person person,
                               BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "auth/registration";
        }
        registrationService.register(person);
        userProfileService.getUserProfile(person.getId());
        return "redirect:/auth/login";
    }


}
