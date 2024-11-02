package com.example.MangaWebSite.service;

import com.example.MangaWebSite.models.Chapter;
import com.example.MangaWebSite.models.Comics;
import com.example.MangaWebSite.repository.ChapterRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ChapterService {

    private final ChapterRepository chapterRepository;

    public void addChapter(Comics comic, Chapter newChapter) {
        Integer lastChapterNumber = chapterRepository.findMaxChapterNumberByComicId(comic.getId());
        newChapter.setChapterNumber(lastChapterNumber != null ? lastChapterNumber + 1 : 1);
        newChapter.setComics(comic);
        chapterRepository.save(newChapter);
    }
}
