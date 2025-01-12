package com.example.MangaWebSite.controller;

import com.example.MangaWebSite.models.Comics;
import com.example.MangaWebSite.repository.ComicsRepository;
import com.example.MangaWebSite.service.ComicsService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("/comics/api")
@AllArgsConstructor
public class AjaxController {

    private final ComicsService comicsService;
    private final ComicsRepository comicsRepository;

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
