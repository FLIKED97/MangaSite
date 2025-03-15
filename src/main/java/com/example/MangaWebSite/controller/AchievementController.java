package com.example.MangaWebSite.controller;

import com.example.MangaWebSite.models.*;
import com.example.MangaWebSite.repository.*;
import com.example.MangaWebSite.security.PersonDetails;
import com.example.MangaWebSite.service.ReadingProgressService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/profile")
@AllArgsConstructor
public class AchievementController {

    private UserProfileRepository userProfileRepository;

    private UserAchievementRepository userAchievementRepository;

    private AchievementRepository achievementRepository;

    private ReadingProgressRepository readingProgressRepository;
    private final ReadingProgressService readingProgressService;

    private PersonRepository personRepository;

    @GetMapping("/achievements")
    public String showAchievementsPage(Model model) {
        // Отримуємо поточного користувача
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        PersonDetails personDetails = (PersonDetails) auth.getPrincipal();
        Person person = personDetails.getPerson();

        // Отримуємо профіль користувача або створюємо новий, якщо не існує
        UserProfile profile = userProfileRepository.findById(person.getId())
                .orElseGet(() -> {
                    Person freshPerson = personRepository.findById(person.getId()).orElseThrow();
                    UserProfile newProfile = new UserProfile();
                    newProfile.setPerson(freshPerson);
                    return userProfileRepository.save(newProfile);
                });

        // Отримуємо всі здобуті досягнення користувача
        List<UserAchievement> userAchievements = userAchievementRepository.findByPersonId(person.getId());

        // Отримуємо всі можливі досягнення
        List<Achievement> allAchievements = achievementRepository.findAll();

        // Створюємо мапу для перегляду прогресу
        Map<AchievementType, Integer> userProgress = new HashMap<>();
        for (AchievementType type : AchievementType.values()) {
            userProgress.put(type, type.extractValue(profile));
        }

        // Обчислюємо XP, необхідний для наступного рівня
        int currentXp = profile.getExperiencePoints();
        int nextLevelXp = (int) Math.pow((profile.getLevel()), 2) * 100;
        int xpProgress = (currentXp * 100) / nextLevelXp;

        // Отримуємо статистику читання
        int completedChapters = readingProgressRepository.countCompletedChaptersByPerson(person.getId());
        int completedComics = readingProgressRepository.countCompletedComicsByPerson(person.getId());

        // Передаємо дані у модель
        model.addAttribute("profile", profile);
        model.addAttribute("userAchievements", userAchievements);
        model.addAttribute("allAchievements", allAchievements);
        model.addAttribute("userProgress", userProgress);
        model.addAttribute("xpProgress", xpProgress);
        model.addAttribute("nextLevelXp", nextLevelXp);
        model.addAttribute("completedChapters", completedChapters);
        model.addAttribute("completedComics", completedComics);

        return "profile/achievements";
    }

    @GetMapping("/statistics")
    public String showStatisticsPage(Model model) {
        // Отримуємо поточного користувача
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        PersonDetails personDetails = (PersonDetails) auth.getPrincipal();
        Person person = personDetails.getPerson();

        // Отримуємо профіль користувача
        UserProfile profile = userProfileRepository.findById(person.getId())
                .orElseGet(() -> {
                    UserProfile newProfile = new UserProfile();
                    newProfile.setPerson(person);
                    return userProfileRepository.save(newProfile);
                });

        // Отримуємо додаткову статистику
        Map<String, Object> readingStats = new HashMap<>();
        readingStats.put("totalChaptersRead", profile.getChaptersRead());
        readingStats.put("totalComicsRead", profile.getComicsRead());
        readingStats.put("commentsPosted", profile.getCommentsPosted());
        readingStats.put("likesReceived", profile.getLikesReceived());
        readingStats.put("daysStreak", profile.getDaysStreak());

        // Отримуємо статистику по тижнях/місяцях для графіків
        Map<String, Integer> weeklyReadingStats = readingProgressService.getWeeklyReadingStats(person.getId());
        Map<String, Integer> monthlyReadingStats = readingProgressService.getMonthlyReadingStats(person.getId());

        model.addAttribute("profile", profile);
        model.addAttribute("readingStats", readingStats);
        model.addAttribute("weeklyStats", weeklyReadingStats);
        model.addAttribute("monthlyStats", monthlyReadingStats);

        return "profile/statistics";
    }
}