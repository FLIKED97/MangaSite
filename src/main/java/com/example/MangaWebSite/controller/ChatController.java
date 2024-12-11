package com.example.MangaWebSite.controller;

import com.example.MangaWebSite.models.Friendship;
import com.example.MangaWebSite.models.Message;
import com.example.MangaWebSite.models.Person;
import com.example.MangaWebSite.security.PersonDetails;
import com.example.MangaWebSite.service.FriendshipService;
import com.example.MangaWebSite.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {

    private final FriendshipService friendshipService;
    private final MessageService messageService;

    @GetMapping
    public String getChatPage(Model model, @AuthenticationPrincipal PersonDetails user) {
        List<Person> friends = friendshipService.getFriends(user.getPerson().getId());
        model.addAttribute("friends", friends);
        return "friends/chat";
    }

    @GetMapping("/messages/{friendId}")
    public ResponseEntity<?> getChatWithFriend(@PathVariable int friendId, @AuthenticationPrincipal PersonDetails user) {
        Person friend = friendshipService.getPersonById(friendId);
        List<Message> messages = messageService.getChatMessages(user.getPerson(), friend);
        Map<String, Object> response = new HashMap<>();
        response.put("friend", friend);
        response.put("messages", messages);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/send")
    public ResponseEntity<?> sendMessage(@RequestParam int friendId, @AuthenticationPrincipal PersonDetails user, @RequestParam String content) {
        Person friend = friendshipService.getPersonById(friendId);
        messageService.sendMessage(user.getPerson(), friend, content);
        return ResponseEntity.ok("Message sent");
    }
}

