package com.example.MangaWebSite.service;

import com.example.MangaWebSite.models.Chapter;
import com.example.MangaWebSite.models.Comics;
import com.example.MangaWebSite.models.Page;
import com.example.MangaWebSite.repository.ChapterRepository;
import com.example.MangaWebSite.repository.ComicsRepository;
import com.example.MangaWebSite.security.PersonDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class ChapterService {
    // Додаємо змінну `uploadDir`, щоб її можна було отримати з application.properties
    @Value("${upload.path}")
    private String uploadDir;
    private final ChapterRepository chapterRepository;

    private final ComicsRepository comicsRepository;

    @Autowired
    public ChapterService(ChapterRepository chapterRepository, ComicsRepository comicsRepository) {
        this.chapterRepository = chapterRepository;
        this.comicsRepository = comicsRepository;
    }

    public String getLatestChapterTitle(int comicId) {
        Optional<Chapter> latestChapter = chapterRepository.findLatestChapterByComicId(comicId);
        return latestChapter.map(Chapter::getTitle).orElse("Немає глав");
    }

    public void addChapterWithPages(int comicId, String title, List<MultipartFile> pageFiles) throws IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        PersonDetails personDetails = (PersonDetails) authentication.getPrincipal();

        Comics comic = comicsRepository.findById(comicId).orElse(null);
        Chapter lastChapter = chapterRepository.findLatestChapterByComicId(comicId).orElse(null);

        Chapter newChapter = new Chapter();
        newChapter.setTitle(title);
        newChapter.setReleaseDate(LocalDate.now());
        newChapter.setPublisher(personDetails.getPerson().getPublisher());
        newChapter.setChapterNumber(lastChapter != null ? lastChapter.getChapterNumber() + 1 : 1);
        newChapter.setComics(comic);

        chapterRepository.save(newChapter);

        String chapterDir = uploadDir + "/comics/" + comicId + "/chapters/" + newChapter.getChapterNumber();
        Files.createDirectories(Paths.get(chapterDir));  // Створюємо директорію один раз

        int pageNumber = 1;
        for (MultipartFile pageFile : pageFiles) {
            if (!pageFile.isEmpty()) {
                String fileName = "page_" + pageNumber + "_" + System.currentTimeMillis() + ".jpg";
                Path filePath = Paths.get(chapterDir, fileName);
                Files.copy(pageFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                Page page = new Page();
                page.setChapter(newChapter);
                page.setImagePath(filePath.toString());
                page.setPageNumber(pageNumber++);
//                pages.add(page);
                newChapter.addPage(page);  // Додаємо сторінку до глави
            }
        }

        chapterRepository.save(newChapter);  // Зберігає главу разом зі сторінками
    }

    public List<Chapter> findAllChapterByComicsId(int comicId) {
        return chapterRepository.findAllByComicsId(comicId);
    }
}
