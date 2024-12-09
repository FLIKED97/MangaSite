package com.example.MangaWebSite.service;

import com.example.MangaWebSite.models.Person;
import com.example.MangaWebSite.models.Role;
import com.example.MangaWebSite.repository.PersonRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class PersonService {

    private final PersonRepository personRepository;
    private final AuthenticationManager authenticationManager;

    public void save(Person person){
        personRepository.save(person);
    }

    @Transactional
    public List<Person> searchByUsername(String term) {
        return personRepository.findByUsernameContaining(term);
    }


    public Person findById(int id) {
        return personRepository.findById(id).orElse(null);
    }

    public void updatePerson(int id, Person updatedPerson) {
        Person existingPerson = personRepository.findById(id).orElse(null);
        if (existingPerson != null) {
            existingPerson.setUsername(updatedPerson.getUsername());
            existingPerson.setEmail(updatedPerson.getEmail());
            personRepository.save(existingPerson);
        }

    }

    public Person findByUsername(String name) {
        return personRepository.findByUsername(name);
    }
}
