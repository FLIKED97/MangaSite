package com.example.MangaWebSite.controller;

import com.example.MangaWebSite.models.Comment;
import com.example.MangaWebSite.models.NewsComment;
import com.example.MangaWebSite.models.Person;
import com.example.MangaWebSite.security.PersonDetails;
import com.example.MangaWebSite.service.CommentService;
import com.example.MangaWebSite.service.NewsCommentService;
import com.example.MangaWebSite.service.PersonService;
import com.example.MangaWebSite.service.UserProfileService;
import lombok.AllArgsConstructor;
import org.springframework.boot.Banner;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/comment")
@AllArgsConstructor
public class CommentController {
    private final CommentService commentService;
    private final NewsCommentService newsCommentService;
    private final PersonService personService;
    private final UserProfileService userProfileService;

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
    @GetMapping("/profile/{id}/comments")
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
        model.addAttribute("activeSection", "comments");

        return "profile/profile"; // Той самий шаблон що і для профілю
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
