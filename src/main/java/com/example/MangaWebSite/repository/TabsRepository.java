package com.example.MangaWebSite.repository;

import com.example.MangaWebSite.models.Person;
import com.example.MangaWebSite.models.Tabs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TabsRepository extends JpaRepository<Tabs, Integer> {
    List<Tabs> findAllByPersonId(int person_id);
}
