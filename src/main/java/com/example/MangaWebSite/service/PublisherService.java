package com.example.MangaWebSite.service;

import com.example.MangaWebSite.models.Person;
import com.example.MangaWebSite.models.Publisher;
import com.example.MangaWebSite.repository.PersonRepository;
import com.example.MangaWebSite.repository.PublisherRepository;
import com.example.MangaWebSite.security.PersonDetails;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import static com.example.MangaWebSite.models.Role.ROLE_PUBLISHER;

@Service
@AllArgsConstructor
@Transactional
public class PublisherService {
    private final PublisherRepository publisherRepository;
    private final PersonService personService;

    private final PersonRepository personRepository;

    public void register(Publisher publisher) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        PersonDetails personDetails = (PersonDetails) authentication.getPrincipal();
        Person person = personRepository.findById(personDetails.getPerson().getId()).orElse(null);

        assert person != null;
        person.setRole(ROLE_PUBLISHER);

        publisher.setCreatedAt(LocalDateTime.now());
        publisher.setAdminId(person.getId()); //TODO Доробити так якщо вже є група, то її видаляють і створюють нову
        publisherRepository.save(publisher);

        person.setPublisher(publisher);
        publisher.getPersons().add(person);

        personRepository.save(person);

        Authentication updatedAuthentication = new UsernamePasswordAuthenticationToken(
                new PersonDetails(person),
                authentication.getCredentials(),
                AuthorityUtils.createAuthorityList(person.getRole().toString())
        );

        SecurityContextHolder.getContext().setAuthentication(updatedAuthentication);

    }

    public Publisher findByPersonId(int personId) {
        return publisherRepository.findPublisherByPersonId(personId);
    }

    public Publisher findById(int id) {
        return publisherRepository.findById(id).orElse(null);
    }

    public void updateGroupName(int id, String name) {
        Publisher publisher = findById(id);
        publisher.setName(name);
        publisherRepository.save(publisher);
    }

    @Transactional
    public List<Publisher> searchByName(String term) {
        return publisherRepository.findByNameContaining(term);
    }

}
