package com.example.MangaWebSite.service;

import com.example.MangaWebSite.models.Chapter;
import com.example.MangaWebSite.models.Comics;
import com.example.MangaWebSite.repository.ChapterRepository;
import com.example.MangaWebSite.repository.ComicsRepository;
import com.example.MangaWebSite.security.PersonDetails;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ChapterService {

    private final ChapterRepository chapterRepository;

    private final ComicsRepository comicsRepository;

    public void addChapter(int comicId, Chapter newChapter) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        PersonDetails personDetails = (PersonDetails) authentication.getPrincipal();

        Integer lastChapterNumber = chapterRepository.findMaxChapterNumberByComicId(comicId);
        newChapter.setChapterNumber(lastChapterNumber != null ? lastChapterNumber + 1 : 1);
        newChapter.setComics(comicsRepository.findById(comicId).orElse(null));
        newChapter.setPublisher(personDetails.getPerson().getPublisher()); //TODO ДОРОБИТИ ФУНКЦІОНАЛ РЕЄСТРАЦІЯ КОРИСТУВАЧА У ПУБЛІКАТОРА КОМІКСІВ
        chapterRepository.save(newChapter);
    }
}
