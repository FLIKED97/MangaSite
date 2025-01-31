package com.example.MangaWebSite.service;

import com.example.MangaWebSite.models.Chapter;
import com.example.MangaWebSite.models.ComicPage;
import com.example.MangaWebSite.models.Comics;
import com.example.MangaWebSite.repository.ChapterRepository;
import com.example.MangaWebSite.repository.ComicsRepository;
import com.example.MangaWebSite.security.PersonDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
        newChapter.setReleaseDate(LocalDateTime.now());
        newChapter.setPublisher(personDetails.getPerson().getPublisher());
        newChapter.setChapterNumber(lastChapter != null ? lastChapter.getChapterNumber() + 1 : 1);
        newChapter.setComics(comic);
        newChapter.setPagesCount(pageFiles.size());

        chapterRepository.save(newChapter);

        String chapterDir = uploadDir + "/comics/" + comicId + "/chapters/" + newChapter.getChapterNumber();
        Files.createDirectories(Paths.get(chapterDir));  // Створюємо директорію один раз

        int pageNumber = 1;
        for (MultipartFile pageFile : pageFiles) {
            if (!pageFile.isEmpty()) {
                String fileName = "page_" + pageNumber + "_" + System.currentTimeMillis() + ".jpg";
                Path filePath = Paths.get(chapterDir, fileName);
                Files.copy(pageFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                ComicPage comicPage = new ComicPage();
                comicPage.setChapter(newChapter);
                comicPage.setImagePath(filePath.toString());
                comicPage.setPageNumber(pageNumber++);
//                pages.add(comicPage);
                newChapter.addPage(comicPage);  // Додаємо сторінку до глави
            }
        }

        chapterRepository.save(newChapter);  // Зберігає главу разом зі сторінками
    }

    public List<Chapter> findAllChapterByComicsId(int comicId) {
        return chapterRepository.findAllByComicsId(comicId)
                .stream()
                .sorted(Comparator.comparing(Chapter::getChapterNumber).reversed())
                .collect(Collectors.toList());
    }
    public Page<Chapter> findChaptersByComicIdPage(int comicId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("releaseDate").descending());
        return chapterRepository.findByComicsId(comicId, pageable);
    }

    public Chapter findById(int id) {
    return chapterRepository.findById(id).orElse(null);
    }

    public Chapter findNextChapter(Chapter currentChapter) {
        return chapterRepository.findFirstByComicsAndChapterNumberGreaterThanOrderByChapterNumberAsc(
                currentChapter.getComics(),
                currentChapter.getChapterNumber()
        );
    }

    // Отримати загальну кількість сторінок для глави
    public int getTotalPagesForChapter() {
        return chapterRepository.findAll().size();
    }

    @Transactional(readOnly = true)
    public Page<Chapter> getNewChaptersInTabs(int page) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Якщо користувач не авторизований, повертаємо порожню сторінку
        if (authentication == null ||
                authentication.getPrincipal().equals("anonymousUser")) {
            Pageable pageable = PageRequest.of(page, 10, Sort.by("releaseDate").descending());
            return Page.empty(pageable);
        }

        // Якщо користувач авторизований
        PersonDetails personDetails = (PersonDetails) authentication.getPrincipal();
        int personId = personDetails.getPerson().getId();
        Pageable pageable = PageRequest.of(page, 10, Sort.by("releaseDate").descending());

        return chapterRepository.findNewChaptersInTabs(personId, pageable);
    }
    @Transactional(readOnly = true)
    public Page<Chapter> getAllNewChapters(int page) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by("releaseDate").descending());
        LocalDateTime oneMonthAgo = LocalDateTime.now().minusDays(70);
        return chapterRepository.findAllNewChapters(oneMonthAgo, pageable);
    }
    public Chapter getFirstChapter(int comicsId) {
        return chapterRepository.findFirstByComicsIdOrderByChapterNumberAsc(comicsId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Перша глава не знайдена"));
    }

    @Transactional
    public Page<Chapter> findChaptersByComicId(int comicId, Pageable pageable) {
        // Додайте логування
        Page<Chapter> chapters = chapterRepository.findByComics_IdOrderByReleaseDateDesc(comicId, pageable);
        System.out.println("Chapters found: " + chapters.getContent().size());
        return chapters;
    }
}
