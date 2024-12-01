package com.example.MangaWebSite.repository;

import com.example.MangaWebSite.models.Publisher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PublisherRepository extends JpaRepository<Publisher, Integer> {
    @Query("SELECT p FROM Publisher p JOIN p.persons persons WHERE persons.id = :personId")
    Publisher findPublisherByPersonId(@Param("personId") int personId);

    @Query("SELECT p FROM Publisher p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :term, '%'))")
    List<Publisher> findByNameContaining(@Param("term") String term);

}
