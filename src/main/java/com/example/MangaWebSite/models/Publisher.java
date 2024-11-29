package com.example.MangaWebSite.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@Entity
@Table(name = "publishers")
public class Publisher {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "admin_id", nullable = false)
    private int adminId;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "publisher", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Person> persons = new ArrayList<>();

    @OneToMany(mappedBy = "publisher", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Chapter> chapters = new ArrayList<>();

    public void addPerson(Person person) {
        // Запобігання нескінченному циклу
        if (persons.contains(person)) return;

        // Додаємо нову особу до списку
        persons.add(person);

        // Встановлюємо посилання на видавця у Person
        person.setPublisher(this);
    }

    public void removePerson(Person person) {
        // Запобігання нескінченному циклу
        if (!persons.contains(person)) return;

        // Видаляємо Person з колекції
        persons.remove(person);

        // Знімаємо посилання на видавця у Person
        person.setPublisher(null);
    }

//    public void addPerson(Person person) {
//        persons.add(person);
//        person.setPublisher(this);  // Встановлення з обох сторін
//    }
}

