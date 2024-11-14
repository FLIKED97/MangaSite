package com.example.MangaWebSite.service;

import com.example.MangaWebSite.models.Chapter;
import com.example.MangaWebSite.models.Comics;
import com.example.MangaWebSite.models.Page;
import com.example.MangaWebSite.repository.ChapterRepository;
import com.example.MangaWebSite.repository.ComicsRepository;
import com.example.MangaWebSite.repository.PageRepository;
import com.example.MangaWebSite.security.PersonDetails;
import lombok.AllArgsConstructor;
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

    private final FileStorageService fileStorageService;

    private final PageRepository pageRepository;

    @Autowired
    public ChapterService(ChapterRepository chapterRepository, ComicsRepository comicsRepository, FileStorageService fileStorageService, PageRepository pageRepository) {
        this.chapterRepository = chapterRepository;
        this.comicsRepository = comicsRepository;
        this.fileStorageService = fileStorageService;
        this.pageRepository = pageRepository;
    }

    public void addChapter(int comicId, Chapter newChapter, List<MultipartFile> pages) {
        // Зберігаємо главу
        Chapter lastChapter = chapterRepository.findLatestChapterByComicId(comicId).orElse(null);
        newChapter.setChapterNumber(lastChapter != null ? lastChapter.getChapterNumber() + 1 : 1);
        chapterRepository.save(newChapter);

        // Зберігаємо кожну сторінку
        int pageNumber = 1;
        for (MultipartFile file : pages) {
            try {
                // Зберігаємо файл у файловій системі
                String filePath = fileStorageService.storeFile(file, comicId, newChapter.getId(), pageNumber);

                // Створюємо сторінку та додаємо до глави
                Page page = new Page();
                page.setChapter(newChapter);
                page.setPageNumber(pageNumber);
                page.setImagePath(filePath);
                pageRepository.save(page);
                pageNumber++;
            } catch (IOException ex) {
                ex.printStackTrace();
                // Обробка помилки зберігання файлу
            }
        }
    }


    public String getLatestChapterTitle(int comicId) {
        Optional<Chapter> latestChapter = chapterRepository.findLatestChapterByComicId(comicId);
        return latestChapter.map(Chapter::getTitle).orElse("Немає глав");
    }

    public void addChapterWithPages(int comicId, String title, List<MultipartFile> pageFiles) throws IOException {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        PersonDetails personDetails = (PersonDetails) authentication.getPrincipal();

        Chapter lastChapter = chapterRepository.findLatestChapterByComicId(comicId).orElse(null);
        Chapter newChapter = new Chapter();
        newChapter.setTitle(title);
        newChapter.setReleaseDate(LocalDate.now());
        newChapter.setPublisher(personDetails.getPerson().getPublisher());
        newChapter.setChapterNumber(lastChapter != null ? lastChapter.getChapterNumber() + 1 : 1);
        newChapter.setComics(comicsRepository.findById(comicId).orElse(null));

        // Збереження нової глави без сторінок
        chapterRepository.save(newChapter);

        // Формування шляху для збереження зображень сторінок
        String chapterDir = uploadDir + "/comics/" + comicId + "/chapters/" + newChapter.getChapterNumber();

        int pageNumber = 1;
        for (MultipartFile pageFile : pageFiles) {
            if (!pageFile.isEmpty()) {
                // Генеруємо унікальне ім'я файлу
                String fileName = "page_" + pageNumber + "_" + System.currentTimeMillis() + ".jpg";
                Path filePath = Paths.get(chapterDir, fileName);

                // Створюємо директорію, якщо її ще немає
                Files.createDirectories(filePath.getParent());

                // Зберігаємо файл
                Files.copy(pageFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                // Створюємо об'єкт `Page` та додаємо його до глави
                Page page = new Page();
                page.setChapter(newChapter);
                page.setImagePath(filePath.toString());
                page.setPageNumber(pageNumber++);

                newChapter.addPage(page);  // Додаємо сторінку до глави
            }
        }

        // Збереження глави разом зі сторінками
        chapterRepository.save(newChapter);
    }
}
