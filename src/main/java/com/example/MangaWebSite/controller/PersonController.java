package com.example.MangaWebSite.controller;

import com.example.MangaWebSite.models.Person;
import com.example.MangaWebSite.security.PersonDetails;
import com.example.MangaWebSite.service.PersonService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;


@Controller
@RequestMapping("/person")
@AllArgsConstructor
public class PersonController {

    private final PersonService personService;
    @GetMapping("/display")
    public String showAllPerson(Model model){
        List<Person> people = personService.findAll();

        model.addAttribute("people", people);
        return "person/personList";
    }
    @GetMapping("/avatar")
    public String showAvatarPage(Model model, @AuthenticationPrincipal PersonDetails user) {
        Person person = personService.findById(user.getPerson().getId());
        model.addAttribute("person", person);
        return "upload-avatar"; // HTML-шаблон для завантаження аватарки
    }


    @PostMapping("/avatar")
    public String uploadAvatar(@RequestParam("avatar") MultipartFile file,
                               RedirectAttributes redirectAttributes,
                               @AuthenticationPrincipal PersonDetails user) {
        try {
            personService.storeAvatarInDb(file, user.getPerson());
            redirectAttributes.addFlashAttribute("message", "Avatar uploaded successfully!");
        } catch (IllegalArgumentException | IOException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/person/avatar";
    }

    // Ендпоінт для віддачі аватарки
    @GetMapping("/avatar/display/{id}")
    @Transactional
    public ResponseEntity<byte[]> getAvatar(@PathVariable Integer id) {
        Person person = personService.findById(id);

        if (person == null || person.getAvatar() == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(person.getAvatarContentType()))
                .body(person.getAvatar());
    }
}