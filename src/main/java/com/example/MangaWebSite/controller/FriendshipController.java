package com.example.MangaWebSite.controller;

import com.example.MangaWebSite.models.Friendship;
import com.example.MangaWebSite.models.Person;
import com.example.MangaWebSite.security.PersonDetails;
import com.example.MangaWebSite.service.FriendshipService;
import com.example.MangaWebSite.service.PersonService;
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

    // Сторінка зі списком друзів
    @GetMapping("/{userId}")
    public String getFriends(@PathVariable int userId, Model model) {
        List<Person> friends = friendshipService.getFriends(userId);

        model.addAttribute("friends", friends);

        return "friends/list"; // Повертає представлення friends/list.html
    }


    @GetMapping("/requests")
    public String viewFriendRequests(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        PersonDetails personDetails = (PersonDetails) authentication.getPrincipal();

        // Запити, які отримав користувач
        List<Friendship> receivedRequests = friendshipService.getReceivedFriendRequests(personDetails.getPerson().getId());

        // Запити, які користувач надіслав
        List<Friendship> sentRequests = friendshipService.getSentFriendRequests(personDetails.getPerson().getId());

        model.addAttribute("receivedRequests", receivedRequests);
        model.addAttribute("sentRequests", sentRequests);

        return "friends/requests"; // Перехід на сторінку friends/requests.html
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

