package com.example.MangaWebSite.service;

import com.example.MangaWebSite.models.ComicPage;
import com.example.MangaWebSite.repository.PageRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class PageService {

    private final PageRepository pageRepository;

    public List<ComicPage> findAllPagesByChapterId(int chapterId) {
        return pageRepository.findAllByChapterId(chapterId);
    }
    public Page<ComicPage> findPagesByChapterId(int chapterId, Pageable pageable) {
        return pageRepository.findByChapterId(chapterId, pageable);
    }

    public Page<ComicPage> findByChapterId(int id, Pageable pageable) {
        try {
            return pageRepository.findByChapterIdOrderByPageNumberAsc(id, pageable);
        } catch (Exception e) {
            return Page.empty(pageable);
        }
    }


//    public boolean hasPages(int chapterId) {
//        return pageRepository.existsByChapterId(chapterId);
//    }
//
//    public long getPagesCount(int chapterId) {
//        return pageRepository.countByChapterId(chapterId);
//    }


//TODO,fdbdbO Реалізувати просте прочитання сторінок, а потім відслідковування останньої прочитаної глави і останнього прочитаного коікса.
}
