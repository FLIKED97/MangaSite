package com.example.MangaWebSite.controller;

import com.example.MangaWebSite.models.Message;
import com.example.MangaWebSite.models.Person;
import com.example.MangaWebSite.security.PersonDetails;
import com.example.MangaWebSite.service.FriendshipService;
import com.example.MangaWebSite.service.MessageService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/share")
@RequiredArgsConstructor
public class ShareController {

    private final MessageService messageService;
    private final FriendshipService friendshipService;

    @PostMapping("/comics/{comicsId}")
    public ResponseEntity<?> shareComics(
            @PathVariable int comicsId,
            @RequestBody List<Integer> friendIds,
            @AuthenticationPrincipal PersonDetails currentUser) {

        List<Message> sharedMessages = messageService.shareComicsWithFriends(
                comicsId,
                friendIds,
                currentUser.getPerson()
        );

        return ResponseEntity.ok(sharedMessages);
    }
}