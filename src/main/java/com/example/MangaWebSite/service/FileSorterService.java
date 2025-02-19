package com.example.MangaWebSite.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Transactional
public class FileSorterService {
    /**
     * Сортує файли на основі переданого порядку.
     *
     * @param fileOrder JSON-строка з порядком файлів.
     * @param files     Список завантажених файлів.
     * @return Відсортований список файлів.
     * @throws IOException Якщо виникла помилка під час розбору JSON.
     */
    public static List<MultipartFile> sortFilesByOrder(String fileOrder, List<MultipartFile> files) throws IOException {
        // Розпарсити порядок файлів
        List<String> order = new ObjectMapper().readValue(fileOrder, new TypeReference<List<String>>() {});

        // Створити мапу з файлами
        Map<String, MultipartFile> fileMap = files.stream()
                .collect(Collectors.toMap(MultipartFile::getOriginalFilename, file -> file));

        // Сортувати файли відповідно до порядку
        return order.stream()
                .map(fileMap::get)
                .filter(Objects::nonNull) // Ігнорувати файли, які не відповідають порядку
                .collect(Collectors.toList());
    }

}