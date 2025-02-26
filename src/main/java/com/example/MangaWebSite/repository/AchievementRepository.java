package com.example.MangaWebSite.repository;

import com.example.MangaWebSite.models.Achievement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AchievementRepository extends JpaRepository<Achievement, Integer> {
    @Query("SELECT a FROM Achievement a WHERE a.id NOT IN " +
            "(SELECT ua.achievement.id FROM UserAchievement ua WHERE ua.person.id = :personId)")
    List<Achievement> findAllNotEarnedByPerson(@Param("personId") int personId);

}
