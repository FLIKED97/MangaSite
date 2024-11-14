package com.example.MangaWebSite.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class FileStorageService {

    @Value("${file.upload-dir}")
    private String uploadDir;

    public String storeFile(MultipartFile file, int comicId, int chapterId, int pageNumber) throws IOException {
        // Створюємо шлях для зберігання файлу
        String folderPath = uploadDir + File.separator + comicId + File.separator + chapterId;
        Path directoryPath = Paths.get(folderPath);

        // Створюємо директорії, якщо вони не існують
        if (!Files.exists(directoryPath)) {
            Files.createDirectories(directoryPath);
        }

        // Зберігаємо файл
        String fileName = pageNumber + "-" + file.getOriginalFilename();
        Path filePath = directoryPath.resolve(fileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // Повертаємо шлях до файлу для збереження в базі даних
        return filePath.toString();
    }
}