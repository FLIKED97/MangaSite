package com.example.MangaWebSite.service;

import com.example.MangaWebSite.models.*;
import com.example.MangaWebSite.repository.ChapterRepository;
import com.example.MangaWebSite.repository.ReadingProgressRepository;
import com.example.MangaWebSite.repository.UserProfileRepository;
import com.example.MangaWebSite.security.PersonDetails;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;


@Service
@AllArgsConstructor
@Transactional
public class ReadingProgressService {
    private final ReadingProgressRepository readingProgressRepository;
    private final UserProfileRepository userProfileRepository;

    private AchievementService achievementService;
    private final ChapterRepository chapterRepository;

    @Transactional
    public void saveOrUpdateProgress(Person person, Chapter chapter, int lastPage) {
        // Get or create the progress record for this specific chapter
        ReadingProgress readingProgress = readingProgressRepository
                .findByPersonIdAndChapterId(person.getId(), chapter.getId())
                .orElse(new ReadingProgress());

        // Set initial values if new
        if (readingProgress.getId() == 0) {
            readingProgress.setPerson(person);
            readingProgress.setChapter(chapter);
        }

        // Update the progress
        readingProgress.setLastPage(lastPage);

        // Check if chapter is completed
        boolean justCompleted = false;
        if (lastPage >= chapter.getTotalPages() && !readingProgress.isCompleted()) {
            readingProgress.setCompleted(true);
            justCompleted = true;
        }

        // Save progress
        readingProgressRepository.save(readingProgress);

        // If chapter was just completed, update user stats and check achievements
        if (justCompleted) {
            updateUserStats(person, chapter);
        }
    }
    @Transactional
    void updateUserStats(Person person, Chapter chapter) {
        // Get or create user profile
        UserProfile profile = userProfileRepository.findById(person.getId())
                .orElse(new UserProfile());

        if (profile.getPersonId() == 0) {
            profile.setPerson(person);
        }

        // Increment chapters read
        profile.setChaptersRead(profile.getChaptersRead() + 1);

        // Check if this is the first chapter of this comic the user has completed
        boolean isFirstChapterOfComic = readingProgressRepository
                .countCompletedChaptersByPersonAndComic(person.getId(), chapter.getComics().getId()) == 1;

        if (isFirstChapterOfComic) {
            profile.setComicsRead(profile.getComicsRead() + 1);
        }

        // Award experience
        int expReward = 10; // Base XP for reading a chapter
        profile.setExperiencePoints(profile.getExperiencePoints() + expReward);

        // Calculate level - simple formula, can be adjusted
        int newLevel = (int) Math.floor(Math.sqrt(profile.getExperiencePoints() / 100)) + 1;
        if (newLevel > profile.getLevel()) {
            profile.setLevel(newLevel);
            // Could trigger level-up notification here
        }

        // Save profile
        userProfileRepository.save(profile);

        // Check for achievements
        achievementService.checkAndAwardAchievements(person);
    }
    public ReadingProgress findByPersonAndComic(Comics comics) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        PersonDetails personDetails = (PersonDetails) authentication.getPrincipal();

        return readingProgressRepository.findByPersonIdAndComicsId(personDetails.getPerson().getId(), comics.getId()).orElse(null);
    }

    public List<ReadingProgress> getRecentlyReadComicsWithProgress(int personId) {
        Pageable pageable = PageRequest.of(0, 10); // отримуємо, наприклад, 10 останніх записів
        List<ReadingProgress> allProgress = readingProgressRepository.findRecentlyReadByPersonId(personId, pageable);

        // Групуємо записи по comicId (отримуємо його через chapter.comics.id)
        Map<Integer, ReadingProgress> latestProgressByComic = new LinkedHashMap<>();
        for (ReadingProgress rp : allProgress) {
            int comicId = rp.getChapter().getComics().getId();
            // Якщо ще немає запису для цього коміксу, додаємо його (через те, що список відсортований DESC, перший запис буде останнім оновленим)
            if (!latestProgressByComic.containsKey(comicId)) {
                latestProgressByComic.put(comicId, rp);
            }
        }
        // Повертаємо список останніх записів для кожного коміксу
        return new ArrayList<>(latestProgressByComic.values());
    }
    
    public Map<Integer, Integer> getComicsProgress(int personId) {
        List<ReadingProgress> allProgress = readingProgressRepository.findByPersonId(personId);
        Map<Integer, Integer> comicsProgress = new HashMap<>();

        for (ReadingProgress progress : allProgress) {
            Comics comic = progress.getChapter().getComics();
            int comicId = comic.getId();

            if (!comicsProgress.containsKey(comicId)) {
                int totalChapters = chapterRepository.countTotalChaptersInComic(comicId);
                int completedChapters = readingProgressRepository.countCompletedChaptersInComic(personId, comicId);

                int progressPercentage = (int) ((completedChapters * 100.0) / totalChapters);
                if (totalChapters == 0) {
                    progressPercentage = 0;
                } else {
                    progressPercentage = (int) ((completedChapters * 100.0) / totalChapters);
                }
                comicsProgress.put(comicId, progressPercentage);
            }
        }

        return comicsProgress;
    }
    public ReadingProgress getReadingProgress(int comicsId, int personId) {
        return readingProgressRepository
                .findFirstByPersonIdAndChapter_Comics_IdOrderByUpdatedAtDesc(personId, comicsId)
                .orElse(null);
    }

    public List<Comics> getCurrentlyReading(int page, int pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize);
        LocalDateTime cutoffTime = LocalDateTime.now().minusDays(1); // Активність за останні 30 хв
        return readingProgressRepository.findCurrentlyReading(cutoffTime, pageable);
    }

    public Page<Comics> getCurrentlyPopularReading(int page, int pageSize) {
        Pageable topFive = PageRequest.of(page, pageSize);
        LocalDateTime cutoffTime = LocalDateTime.now().minusDays(1);
        return readingProgressRepository.findCurrentlyPopularReading(cutoffTime, topFive);
    }

    public ReadingProgress findByPersonAndChapter(Chapter chapter) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        PersonDetails personDetails = (PersonDetails) authentication.getPrincipal();
        return readingProgressRepository
                .findByPersonIdAndChapterId(personDetails.getPerson().getId(), chapter.getId())
                .orElse(null);
    }

}
