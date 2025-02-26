package com.example.MangaWebSite.service;

import com.example.MangaWebSite.models.Comics;
import com.example.MangaWebSite.models.Person;
import com.example.MangaWebSite.models.Tabs;
import com.example.MangaWebSite.repository.TabsRepository;
import com.example.MangaWebSite.security.PersonDetails;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class TabsService {
    private final TabsRepository tabsRepository;

    public List<Tabs> findByPersonId(int id){

        return tabsRepository.findAllByPersonId(id);
    }
    @Transactional
    public Tabs findById(int tabId) {
        return tabsRepository.findById(tabId).orElse(null);
    }

    @Transactional
    public int saveWithRedirect(Tabs tab) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        PersonDetails personDetails = (PersonDetails) authentication.getPrincipal();
        tab.setAddedAt(LocalDateTime.now());
        tab.setPerson(personDetails.getPerson());
        tabsRepository.save(tab);

        return tab.getPerson().getId();
    }

    public void save(Tabs tab) {
        tabsRepository.save(tab);
    }
    public List<Comics> findComicsByTabId(int tabId) {
        return tabsRepository.findById(tabId)
                .map(Tabs::getComics)
                .orElse(Collections.emptyList());
    }

    public void deleteById(int id) {
        tabsRepository.deleteById(id);
    }

    public List<Tabs> findTabsByPersonId(int id) {
        return tabsRepository.findAllByPersonId(id);
    }

    public List<Integer> getBookmarkedComicsIds(Person person) {
        List<Tabs> userTabs = tabsRepository.findByPerson(person);
        return userTabs.stream()
                .flatMap(tab -> tab.getComics().stream())
                .map(Comics::getId)
                .distinct()
                .collect(Collectors.toList());
    }

    public List<Tabs> getTabsWithComicsCount(int personId) {
        return tabsRepository.findByPersonIdWithComics(personId);
    }

    public List<Tabs> getAllTabs() {
        return tabsRepository.findAll();
    }
}
