package com.example.MangaWebSite.service;

import com.example.MangaWebSite.models.Person;
import com.example.MangaWebSite.models.UserProfile;
import com.example.MangaWebSite.repository.PersonRepository;
import com.example.MangaWebSite.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;

@Service
@RequiredArgsConstructor
@Transactional
public class UserProfileService {
    private final UserProfileRepository userProfileRepository;
    private final PersonRepository personRepository;


    public UserProfile getUserProfile(int personId) {
        return userProfileRepository.findById(personId)
                .orElseGet(() -> {
                    Person freshPerson = personRepository.findById(personId).orElseThrow();
                    UserProfile newProfile = new UserProfile();
                    newProfile.setPerson(freshPerson);
                    return userProfileRepository.save(newProfile);
                });
    }

    public int getNextLevelXp(UserProfile profile) {
        return (int) Math.pow((profile.getLevel()), 2) * 100;
    }

    public int getXpProgress(UserProfile profile) {
        int nextLevelXp = getNextLevelXp(profile);
        return (profile.getExperiencePoints() * 100) / nextLevelXp;
    }

    public void addProfileInfoToModel(Model model, int personId) {
        UserProfile profile = getUserProfile(personId);
        model.addAttribute("profile", profile);
        model.addAttribute("nextLevelXp", getNextLevelXp(profile));
        model.addAttribute("xpProgress", getXpProgress(profile));
    }
}