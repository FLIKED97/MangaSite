package com.example.MangaWebSite.repository;

import com.example.MangaWebSite.models.Comics;
import com.example.MangaWebSite.models.Person;
import com.example.MangaWebSite.models.Tabs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TabsRepository extends JpaRepository<Tabs, Integer> {
    List<Tabs> findAllByPersonId(int person_id);

    List<Tabs> findByPerson(Person person);

    @Query("SELECT t FROM Tabs t LEFT JOIN FETCH t.comics WHERE t.person.id = :personId")
    List<Tabs> findByPersonIdWithComics(@Param("personId") int personId);

}
