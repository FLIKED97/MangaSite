package com.example.MangaWebSite.service;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@Service
public class FileStorageService {

    private final Path baseStorageLocation;

    public FileStorageService() {
        // Встановлюємо базову директорію для збереження файлів
        this.baseStorageLocation = Paths.get("uploads").toAbsolutePath().normalize();
        try {
            // Створюємо базову директорію, якщо вона не існує
            Files.createDirectories(this.baseStorageLocation);
        } catch (IOException ex) {
            throw new RuntimeException("Не вдалося створити базову директорію для збереження файлів.", ex);
        }
    }

    public String saveFile(MultipartFile file) throws IOException {
        // Очищаємо ім'я файлу
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());

        // Перевіряємо ім'я файлу на наявність небажаних послідовностей
        if (fileName.contains("..")) {
            throw new IOException("Ім'я файлу містить заборонену послідовність: " + fileName);
        }

        // Створюємо директорію uploads/book, якщо її ще немає
        Path bookBaseDir = this.baseStorageLocation.resolve("book");
        if (!Files.exists(bookBaseDir)) {
            Files.createDirectories(bookBaseDir);
        }

        // Генеруємо унікальне ім'я для папки книги
        String uniqueBookFolder = UUID.randomUUID().toString();
        Path bookFolderPath = bookBaseDir.resolve(uniqueBookFolder);
        Files.createDirectories(bookFolderPath);

        // Визначаємо шлях для збереження файлу книги в цій папці
        Path targetLocation = bookFolderPath.resolve(fileName);
        Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

        // Повертаємо шлях до збереженого файлу (абсолютний шлях або відносний, як потрібно)
        return targetLocation.toString();
    }
}
