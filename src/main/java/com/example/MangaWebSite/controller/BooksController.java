package com.example.MangaWebSite.controller;

import com.example.MangaWebSite.models.Comics;
import com.example.MangaWebSite.models.PublicationType;
import com.example.MangaWebSite.service.ComicsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;

@RestController
@RequestMapping("/books")
public class BooksController {

    @Autowired
    private ComicsService comicsService;

    /**
     * Endpoint для відображення книги (наприклад, PDF) в iframe.
     */
    @GetMapping("/read/{id}")
    public ResponseEntity<Resource> readBook(@PathVariable("id") int id) {
        Comics comic = comicsService.findById(id).orElse(null);
        if (comic == null || comic.getPublicationType() != PublicationType.BOOK) {
            return ResponseEntity.notFound().build();
        }
        File file = new File(comic.getBookFilePath());
        if (!file.exists()) {
            return ResponseEntity.notFound().build();
        }
        FileSystemResource resource = new FileSystemResource(file);
        HttpHeaders headers = new HttpHeaders();
        // Дозволяємо вбудовування в iframe
        headers.add("X-Frame-Options", "SAMEORIGIN");
        // Встановлюємо Content-Type як PDF
        headers.setContentType(MediaType.APPLICATION_PDF);
        // Вказуємо, що файл має відображатись inline
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + file.getName() + "\"");
        return ResponseEntity.ok().headers(headers).body(resource);
    }


    /**
     * Endpoint для завантаження книги.
     */
    @GetMapping("/download/{id}")
    public ResponseEntity<Resource> downloadBook(@PathVariable("id") int id) {
        Comics comic = comicsService.findById(id).orElse(null);
        if (comic == null || comic.getPublicationType() != null && !comic.getPublicationType().name().equals("BOOK")) {
            return ResponseEntity.notFound().build();
        }
        File file = new File(comic.getBookFilePath());
        if (!file.exists()) {
            return ResponseEntity.notFound().build();
        }
        FileSystemResource resource = new FileSystemResource(file);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        // Для завантаження встановлюємо Content-Disposition як attachment
        headers.setContentDispositionFormData("attachment", file.getName());
        return ResponseEntity.ok().headers(headers).body(resource);
    }
}
