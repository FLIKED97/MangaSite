package com.example.MangaWebSite.service;

import com.example.MangaWebSite.models.Achievement;
import com.example.MangaWebSite.models.Person;
import com.example.MangaWebSite.models.UserAchievement;
import com.example.MangaWebSite.models.UserProfile;
import com.example.MangaWebSite.repository.AchievementRepository;
import com.example.MangaWebSite.repository.UserAchievementRepository;
import com.example.MangaWebSite.repository.UserProfileRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class AchievementService {

    private AchievementRepository achievementRepository;

    private UserAchievementRepository userAchievementRepository;

    private UserProfileRepository userProfileRepository;

    @Transactional
    public void checkAndAwardAchievements(Person person) {
        UserProfile profile = userProfileRepository.findById(person.getId())
                .orElseThrow(() -> new EntityNotFoundException("User profile not found"));

        // Отримуємо всі ще не здобуті досягнення
        List<Achievement> availableAchievements = achievementRepository.findAllNotEarnedByPerson(person.getId());

        for (Achievement achievement : availableAchievements) {
            // Отримуємо поточне значення метрики користувача за типом досягнення
            int currentValue = achievement.getType().extractValue(profile);

            // Перевіряємо, чи досягнуто порогу
            if (currentValue >= achievement.getThreshold()) {
                // Створюємо запис про здобуте досягнення
                UserAchievement userAchievement = new UserAchievement();
                userAchievement.setPerson(person);
                userAchievement.setAchievement(achievement);
                userAchievementRepository.save(userAchievement);

                // Нараховуємо досвід
                profile.setExperiencePoints(profile.getExperiencePoints() + achievement.getExperienceReward());

                // Перераховуємо рівень
                int newLevel = calculateLevel(profile.getExperiencePoints());
                if (newLevel > profile.getLevel()) {
                    profile.setLevel(newLevel);
                    // TODO: Сповіщення про підвищення рівня
                }

                // TODO: Сповіщення про нове досягнення
            }
        }

        // Зберігаємо оновлений профіль
        userProfileRepository.save(profile);
    }

    private int calculateLevel(int experiencePoints) {
        return (int) Math.floor(Math.sqrt(experiencePoints / 100)) + 1;
    }
}