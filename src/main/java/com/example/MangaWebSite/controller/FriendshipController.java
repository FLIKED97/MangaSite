package com.example.MangaWebSite.controller;

import com.example.MangaWebSite.models.Friendship;
import com.example.MangaWebSite.models.Person;
import com.example.MangaWebSite.security.PersonDetails;
import com.example.MangaWebSite.service.FriendshipService;
import com.example.MangaWebSite.service.PersonService;
import com.example.MangaWebSite.service.UserProfileService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/friends")
@AllArgsConstructor
public class FriendshipController {

    private final FriendshipService friendshipService;

    private final PersonService personService;

    private final UserProfileService userProfileService;
    // Сторінка зі списком друзів
    @GetMapping("/{userId}")
    public String getFriends(@PathVariable int userId, Model model) {
        // Отримуємо поточного користувача
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        PersonDetails personDetails = (PersonDetails) authentication.getPrincipal();

        // Завантажуємо всі необхідні дані
        List<Person> friends = friendshipService.getFriends(userId);
        List<Friendship> receivedRequests = friendshipService.getReceivedFriendRequests(personDetails.getPerson().getId());
        List<Friendship> sentRequests = friendshipService.getSentFriendRequests(personDetails.getPerson().getId());

        // Додаємо всі дані в модель
        model.addAttribute("friends", friends);
        model.addAttribute("receivedRequests", receivedRequests);
        model.addAttribute("sentRequests", sentRequests);

        return "friends/list";
    }
    @GetMapping("/profile/{id}/friends")
    public String showFriends(@PathVariable int id, Model model) {
        // Завантажити користувача з бази за ID
        Person person = personService.findById(id);

        if (person == null) {
            // Обробка помилки, якщо користувач не знайдений
            return "error/404";
        }

        model.addAttribute("person", person);

        // Додавання профільної інформації через сервіс
        userProfileService.addProfileInfoToModel(model, id);

        // Додаємо спеціальний атрибут для позначення, що потрібно активувати вкладку "Друзі"
        model.addAttribute("activeSection", "friends");

        return "profile/profile"; // Той самий шаблон що і для профілю
    }
    @GetMapping("/requests")
    public String viewFriendRequests(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        PersonDetails personDetails = (PersonDetails) authentication.getPrincipal();

        // Запити, які отримав користувач
        List<Friendship> receivedRequests = friendshipService.getReceivedFriendRequests(personDetails.getPerson().getId());

        model.addAttribute("receivedRequests", receivedRequests);

        return "friends/requests"; // Перехід на сторінку friends/requests.html
    }
    @GetMapping("/sentrequests")
    public String viewSentRequests(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        PersonDetails personDetails = (PersonDetails) authentication.getPrincipal();

        // Запити, які користувач надіслав
        List<Friendship> sentRequests = friendshipService.getSentFriendRequests(personDetails.getPerson().getId());

        model.addAttribute("sentRequests", sentRequests);

        return "friends/sentRequests";
    }

    // Відправити запит на дружбу
    @PostMapping("/request/{friendId}")
    public ResponseEntity<String> sendFriendRequest(@PathVariable int friendId) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        PersonDetails personDetails = (PersonDetails) authentication.getPrincipal();
        Person friend = personService.findById(friendId);

        if (friendshipService.areFriends(personDetails.getPerson().getId(), friendId)) {
            return ResponseEntity.badRequest().body("Ви вже друзі.");
        }

        friendshipService.createFriendRequest(personDetails.getPerson().getId(), friend.getId());
        return ResponseEntity.ok("Запит на дружбу успішно відправлено.");
    }

    // Підтвердити запит на дружбу
    @PostMapping("/accept")
    public String acceptFriendRequest(@RequestParam int requestId, @RequestParam int userId, RedirectAttributes redirectAttributes) {
        try {
            friendshipService.acceptFriendRequest(requestId);
            redirectAttributes.addFlashAttribute("message", "Запит на дружбу підтверджено!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/friends/" + userId;
    }

    // Відхилити запит на дружбу
    @PostMapping("/decline")
    public String declineFriendRequest(@RequestParam int requestId, @RequestParam int userId, RedirectAttributes redirectAttributes) {
        try {
            friendshipService.declineFriendRequest(requestId);
            redirectAttributes.addFlashAttribute("message", "Запит на дружбу відхилено!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/friends/" + userId;
    }
}

