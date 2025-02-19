package com.example.MangaWebSite.service;

import com.example.MangaWebSite.models.Comics;
import com.example.MangaWebSite.models.Person;
import com.example.MangaWebSite.models.Rating;
import com.example.MangaWebSite.repository.ComicsRepository;
import com.example.MangaWebSite.repository.RatingRepository;
import com.example.MangaWebSite.security.PersonDetails;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@AllArgsConstructor
@Transactional
public class RatingService {

    private final RatingRepository ratingRepository;

    private final ComicsRepository comicsRepository;
    public void saveOrUpdateComicsRating(int comicsId, int rating) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        PersonDetails personDetails = (PersonDetails) authentication.getPrincipal();
        Person person = personDetails.getPerson();
        Comics comics = comicsRepository.findById(comicsId).orElse(null);

        Rating ratingEntity = ratingRepository
                .findByComicsIdAndPersonId(comicsId, person.getId())
                .orElseGet(() -> {
                    Rating newRating = new Rating();
                    newRating.setComics(comics);
                    newRating.setPerson(person);
                    newRating.setCreatedAt(LocalDateTime.now());
                    return newRating;
                });

        ratingEntity.setRating(rating);
        ratingRepository.save(ratingEntity);
    }


    public Integer getUserRating(int comicId, int id) {
        return ratingRepository.findByComicsIdAndPersonId(comicId, id)
                .map(Rating::getRating)
                .orElse(null);
    }
}
