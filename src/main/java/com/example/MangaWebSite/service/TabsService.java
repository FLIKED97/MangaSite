package com.example.MangaWebSite.service;

import com.example.MangaWebSite.models.Comics;
import com.example.MangaWebSite.models.Tabs;
import com.example.MangaWebSite.repository.TabsRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@AllArgsConstructor
public class TabsService {
    private final TabsRepository tabsRepository;

    public List<Tabs> findByPersonId(int id){

        return tabsRepository.findAllByPersonId(id);
    }
    @Transactional
    public Tabs findById(int tabId) {
        return tabsRepository.findById(tabId).orElse(null);
    }

    public void save(Tabs tab) {
        tabsRepository.save(tab);
    }


    public List<Comics> findComicsByTabId(int tabId) {
        return tabsRepository.findById(tabId)
                .map(Tabs::getComics)
                .orElse(Collections.emptyList());
    }
}
