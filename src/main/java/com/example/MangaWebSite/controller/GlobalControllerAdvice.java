package com.example.MangaWebSite.controller;

import com.example.MangaWebSite.models.Person;
import com.example.MangaWebSite.security.PersonDetails;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalControllerAdvice {

    @ModelAttribute("person")
    public Person getGlobalPerson(@AuthenticationPrincipal PersonDetails personDetails) {
        return personDetails != null ? personDetails.getPerson() : null;
    }
}


