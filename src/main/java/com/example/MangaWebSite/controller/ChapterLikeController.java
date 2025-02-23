package com.example.MangaWebSite.controller;

import com.example.MangaWebSite.models.ChapterLike;
import com.example.MangaWebSite.security.PersonDetails;
import com.example.MangaWebSite.service.ChapterLikeService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Controller
@AllArgsConstructor
@RequestMapping("/chapter")
public class ChapterLikeController {
    private final ChapterLikeService chapterLikeService;

    @PostMapping("/like")
    public ResponseEntity<?> likeChapter(@RequestParam("chapterId") int chapterId,
                                         @AuthenticationPrincipal PersonDetails user) {
        ChapterLike chapterLike = chapterLikeService.likeChapter(chapterId, user.getPerson());
        long count = chapterLikeService.countLikes(chapterId);
        Map<String, Object> response = new HashMap<>();
        response.put("liked", true);
        response.put("likesCount", count);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/unlike")
    public ResponseEntity<?> unLikeChapter(@RequestParam("chapterId") int chapterId,
                                           @AuthenticationPrincipal PersonDetails user) {
        chapterLikeService.unLikeChapter(chapterId, user.getPerson());
        long count = chapterLikeService.countLikes(chapterId);
        Map<String, Object> response = new HashMap<>();
        response.put("liked", false);
        response.put("likesCount", count);
        return ResponseEntity.ok(response);
    }
}
