package com.example.MangaWebSite.controller;

import com.example.MangaWebSite.models.Comics;
import com.example.MangaWebSite.service.ComicsService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/comics/api")
@AllArgsConstructor
public class AjaxController {
    private final ComicsService comicsService;

    @GetMapping("/search")
    @ResponseBody
    @Transactional(readOnly = true)
    public ResponseEntity<?> searchComics(
            @RequestParam(name = "search", required = false, defaultValue = "") String search,
            @RequestParam(name = "sortBy", required = false, defaultValue = "rating") String sortBy,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size
    ) {
        Page<Comics> comicsPage = comicsService.searchComics(search, sortBy, page, size);

        Map<String, Object> response = new HashMap<>();
        List<Map<String, Object>> content = comicsPage.getContent().stream()
                .map(comics -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", comics.getId());
                    map.put("title", comics.getTitle());
                    map.put("averageRating", comics.getAverageRating());
                    map.put("comicsTypeDisplay", comics.getComicsType().getDisplayName());
                    map.put("viewCount", comics.getViewCount());
                    map.put("description", comics.getDescription());
                    // додайте інші потрібні поля
                    return map;
                })
                .collect(Collectors.toList());

        response.put("content", content);
        response.put("totalPages", comicsPage.getTotalPages());
        response.put("totalElements", comicsPage.getTotalElements());
        response.put("last", comicsPage.isLast());

        return ResponseEntity.ok(response);
    }
}
