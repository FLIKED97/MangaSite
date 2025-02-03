package com.example.MangaWebSite.controller;

import com.example.MangaWebSite.models.Comics;
import com.example.MangaWebSite.service.ComicsService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/comics/api")
@AllArgsConstructor
public class AjaxController {
    private final ComicsService comicsService;

    @GetMapping("/search")
    @ResponseBody
    public Page<Comics> searchComics(
            @RequestParam(name = "search", required = false, defaultValue = "") String search,
            @RequestParam(name = "sortBy", required = false, defaultValue = "rating") String sortBy,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size
    ) {
        return comicsService.searchComics(search, sortBy, page, size);
    }
}
