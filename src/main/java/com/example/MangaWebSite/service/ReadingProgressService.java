package com.example.MangaWebSite.service;

import com.example.MangaWebSite.models.*;
import com.example.MangaWebSite.repository.ReadingProgressRepository;
import com.example.MangaWebSite.security.PersonDetails;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@AllArgsConstructor
@Transactional
public class ReadingProgressService {
    private final ReadingProgressRepository readingProgressRepository;

    public void saveOrUpdateProgress(Person person, Chapter chapter, int lastPage) {
        ReadingProgress readingProgress = readingProgressRepository
                .findByPersonIdAndComicsId(person.getId(), chapter.getComics().getId())
                .orElse(null);

        if (readingProgress == null) {
            readingProgress = new ReadingProgress();
            readingProgress.setPerson(person);
            readingProgress.setComics(chapter.getComics());
        }

        readingProgress.setChapter(chapter);
        readingProgress.setLastPage(lastPage);

        readingProgressRepository.save(readingProgress);
    }
    public ReadingProgress findByPersonAndComic(Comics comics) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        PersonDetails personDetails = (PersonDetails) authentication.getPrincipal();

        return readingProgressRepository.findByPersonIdAndComicsId(personDetails.getPerson().getId(), comics.getId()).orElse(null);
    }

    public List<ReadingProgress> getRecentlyReadComicsWithProgress(int id) {
        Pageable topFive = PageRequest.of(0, 10); // Сторінка 0, розмір 10

        return readingProgressRepository.findRecentlyReadByPersonId(id, topFive);
    }
}
