package com.example.MangaWebSite.service;

import com.example.MangaWebSite.models.Tabs;
import com.example.MangaWebSite.repository.TabsRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class TabsService {
    private final TabsRepository tabsRepository;

    public List<Tabs> findByPersonId(int id){

        return tabsRepository.findAllByPersonId(id);
    }
}
