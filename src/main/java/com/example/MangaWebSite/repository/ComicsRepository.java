package com.example.MangaWebSite.repository;

import com.example.MangaWebSite.models.Comics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ComicsRepository extends JpaRepository<Comics, Integer> {

}
