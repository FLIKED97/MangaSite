package com.example.MangaWebSite.service;

import com.example.MangaWebSite.models.Person;
import com.example.MangaWebSite.models.Role;
import com.example.MangaWebSite.repository.PersonRepository;
import com.example.MangaWebSite.security.PersonDetails;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.List;

@Service
@AllArgsConstructor
public class PersonService {

    private final PersonRepository personRepository;
    private final AuthenticationManager authenticationManager;
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB
    private static final String UPLOAD_DIR = "uploads/persons/";

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

    public void uploadAvatar(MultipartFile file, PersonDetails userDetails) throws IOException {
        // Validate file
        validateFile(file);

        // Find person
        Person person = personRepository.findById(userDetails.getPerson().getId())
                .orElseThrow(() -> new RuntimeException("Person not found"));

        // Create upload directory
        Path personPath = createPersonUploadDirectory(person.getId());

        // Generate unique filename
        String fileName = "avatar_" + System.currentTimeMillis() + ".jpg";
        Path filePath = personPath.resolve(fileName);

        // Save file
        Files.copy(file.getInputStream(), filePath);

        // Update person's avatar path
        person.setAvatarPath(filePath.toString());
        personRepository.save(person);
    }

    private void validateFile(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Please select a file");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("File too large. Maximum size is 5MB");
        }

        if (!file.getContentType().startsWith("image/")) {
            throw new IllegalArgumentException("Please upload an image");
        }
    }

    private Path createPersonUploadDirectory(int personId) throws IOException {
        Path personPath = Paths.get(UPLOAD_DIR + personId);
        if (!Files.exists(personPath)) {
            Files.createDirectories(personPath);
        }
        return personPath;
    }
}
