package com.example.MangaWebSite.repository;

import com.example.MangaWebSite.models.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Integer> {

    Optional<Rating> findByComicsIdAndPersonId(int comicId, int userId);
}
