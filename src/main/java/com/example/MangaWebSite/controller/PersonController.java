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


@Controller
@RequestMapping("/person")
@AllArgsConstructor
public class PersonController {

    private PersonService personService;

        @GetMapping("/avatar")
        public String showAvatarPage(Model model, @AuthenticationPrincipal PersonDetails user) {
            Person person = personService.findById(user.getPerson().getId());
            model.addAttribute("person", person);
            return "upload-avatar";
        }

        @PostMapping("/avatar")
        @Transactional
        public String uploadAvatar(@RequestParam("avatar") MultipartFile file,
                                   RedirectAttributes redirectAttributes,
                                   @AuthenticationPrincipal PersonDetails user) {
            try {
                if (file.isEmpty()) {
                    redirectAttributes.addFlashAttribute("error", "Будь ласка, виберіть файл");
                    return "redirect:/person/avatar";
                }

                if (file.getSize() > 5 * 1024 * 1024) { // 5MB
                    redirectAttributes.addFlashAttribute("error", "Файл завеликий. Максимальний розмір - 5MB");
                    return "redirect:/person/avatar";
                }

                if (!file.getContentType().startsWith("image/")) {
                    redirectAttributes.addFlashAttribute("error", "Будь ласка, завантажте зображення");
                    return "redirect:/person/avatar";
                }

                Person person = personService.findById(user.getPerson().getId());
                person.setAvatar(file.getBytes());
                person.setAvatarType(file.getContentType());
                personService.save(person);

                redirectAttributes.addFlashAttribute("message", "Аватар успішно завантажено!");
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("error", "Помилка при завантаженні файлу: " + e.getMessage());
            }

            return "redirect:/person/avatar";
        }

        @GetMapping("/avatar/display/{id}")
        @ResponseBody
        public ResponseEntity<byte[]> getAvatar(@PathVariable int id) {
            Person person = personService.findById(id);

            if (person != null && person.getAvatar() != null) {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.parseMediaType(person.getAvatarType()));
                return new ResponseEntity<>(person.getAvatar(), headers, HttpStatus.OK);
            }

            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
}