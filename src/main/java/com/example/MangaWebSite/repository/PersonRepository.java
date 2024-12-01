package com.example.MangaWebSite.repository;

import com.example.MangaWebSite.models.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PersonRepository extends JpaRepository<Person, Integer> {
    Optional<Person> findByEmail(String email);

    @Query("SELECT p FROM Person p WHERE LOWER(p.username) LIKE LOWER(CONCAT('%', :term, '%'))")
    List<Person> findByUsernameContaining(@Param("term") String term);
}
