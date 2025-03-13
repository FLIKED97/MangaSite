package com.example.MangaWebSite.controller;

import com.example.MangaWebSite.models.Publisher;
import com.example.MangaWebSite.security.PersonDetails;
import com.example.MangaWebSite.service.PublisherService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/publisher")
@AllArgsConstructor
public class PublisherController {
    private final PublisherService publisherService;

    @GetMapping("/register")
    public String showPublisherRegistrationForm(Model model){
        model.addAttribute("publisher", new Publisher());
        return "publisher/register";
    }

    @PostMapping("/register")
    public String registerPublisher(@ModelAttribute("publisher") Publisher publisher) {
        publisherService.register(publisher);
        return "redirect:/main";
    }
    @GetMapping("/group/{id}")
    public String infoGroup(@PathVariable int id, Model model) {
        Publisher publisher = publisherService.findById(id);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        PersonDetails personDetails = (PersonDetails) authentication.getPrincipal();
        //  Publisher publisher = publisherService.findByPersonId(personDetails.getPerson().getId()); //TODO ВИКОРИСТАТИ ДЛЯ ГОЛОВНОЇ СТОРІНКИ

        model.addAttribute("publisher", publisher);
        if(publisher.getAdminId() == personDetails.getPerson().getId()) {
            boolean isLeader = true;
            model.addAttribute("isLeader", isLeader);
        }
        return "publisher/group";
    }
    @PostMapping("/group/{id}/update")
    public String updateNameGroup(@PathVariable int id, @RequestParam("name") String name){
        publisherService.updateGroupName(id, name);
        return "redirect:/publisher/group/" + id;

    }
    @Transactional
    @GetMapping("/list")
    public String showAllPublishers(Model model,
                                    @RequestParam(defaultValue = "") String search) {
        List<Publisher> publishers;
        if (!search.isEmpty()) {
            publishers = publisherService.searchByName(search);
        } else {
            publishers = publisherService.findAll();
        }
        model.addAttribute("publishers", publishers);
        return "publisher/list";
    }

}
