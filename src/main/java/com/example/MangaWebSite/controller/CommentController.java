package com.example.MangaWebSite.controller;

import com.example.MangaWebSite.models.Comment;
import com.example.MangaWebSite.models.NewsComment;
import com.example.MangaWebSite.security.PersonDetails;
import com.example.MangaWebSite.service.CommentService;
import com.example.MangaWebSite.service.NewsCommentService;
import lombok.AllArgsConstructor;
import org.springframework.boot.Banner;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/comment")
@AllArgsConstructor
public class CommentController {
    private final CommentService commentService;
    private final NewsCommentService newsCommentService;

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
    public String showComment(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        PersonDetails personDetails = (PersonDetails) authentication.getPrincipal();
        int personId = personDetails.getPerson().getId();

        List<Comment> comicsComments = commentService.getAllCommentPerson();
        List<NewsComment> newsComments = newsCommentService.getAllNewsCommentsByPersonId(personId);

        model.addAttribute("comicsComments", comicsComments);
        model.addAttribute("newsComments", newsComments);
        return "comment/show";
    }
}
