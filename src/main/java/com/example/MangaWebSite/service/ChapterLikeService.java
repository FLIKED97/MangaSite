package com.example.MangaWebSite.service;

import com.example.MangaWebSite.exception.ResourceNotFoundException;
import com.example.MangaWebSite.models.Chapter;
import com.example.MangaWebSite.models.ChapterLike;
import com.example.MangaWebSite.models.Person;
import com.example.MangaWebSite.repository.ChapterLikeRepository;
import com.example.MangaWebSite.repository.ChapterRepository;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@AllArgsConstructor
@Transactional
@Slf4j
public class ChapterLikeService {
    private final ChapterLikeRepository chapterLikeRepository;
    private final ChapterRepository chapterRepository;

    public ChapterLike likeChapter(int chapterId, Person person) {
        // Перевіряємо, чи вже є лайк від цього користувача для даної глави
        ChapterLike existing = chapterLikeRepository.findByChapterIdAndPersonId(chapterId, person.getId());
        if (existing != null) {
            return existing; // або кинути помилку, якщо не бажаємо повторного лайку
        }
        Chapter chapter = chapterRepository.findById(chapterId)
                .orElseThrow(() -> new ResourceNotFoundException("Chapter not found"));
        ChapterLike chapterLike = new ChapterLike();
        chapterLike.setLikedAt(LocalDateTime.now());
        chapterLike.setChapter(chapter);
        chapterLike.setPerson(person);
        return chapterLikeRepository.save(chapterLike);
    }

    public void unLikeChapter(int chapterId, Person person) {
        ChapterLike existing = chapterLikeRepository.findByChapterIdAndPersonId(chapterId, person.getId());
        if (existing != null) {
            chapterLikeRepository.delete(existing);
        }
    }

    public long countLikes(int chapterId) {
        Chapter chapter = chapterRepository.findById(chapterId)
                .orElseThrow(() -> new ResourceNotFoundException("Chapter not found"));
        return chapterLikeRepository.countByChapter(chapter);
    }

    public Optional<ChapterLike> findLikeChapter(int chapterId, Person person){
        return Optional.ofNullable(chapterLikeRepository.findByChapterIdAndPersonId(chapterId, person.getId()));
    }
}
