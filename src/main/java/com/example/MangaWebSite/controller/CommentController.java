package com.example.MangaWebSite.controller;

import com.example.MangaWebSite.service.CommentService;
import lombok.AllArgsConstructor;
import org.springframework.boot.Banner;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/comment")
@AllArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @GetMapping("/getByComics")
    public String getCommentsByComicsId(@RequestParam("comicsId") int comicsId, Model model) {
        model.addAttribute("comments", commentService.getCommentsByComicsId(comicsId));
        return "comics/comments-section :: commentsList"; // Фрагмент для виводу
    }

    @PostMapping("/add")
    public String createNewCommentInComics(Model model,
                                           @RequestParam("comicsId") int comicsId,
                                           @RequestParam("textComment") String textComment){

        commentService.createCommentInComics(comicsId, textComment);

        return "redirect:/comics/" + comicsId;
    }

    @GetMapping("/show")
    public String showComment(Model model){
        model.addAttribute("comment", commentService.getAllCommentPerson());
        return "comment/show";
    }
}
