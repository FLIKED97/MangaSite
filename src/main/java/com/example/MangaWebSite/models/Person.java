package com.example.MangaWebSite.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@Entity
@Table(name = "person")
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role;

    @Column(name = "created_at", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "person", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore // Уникаємо серіалізації
    private List<Comment> comments;

    @OneToMany(mappedBy = "person", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Tabs> tabs;  // У користувача може бути багато закладок

    @OneToMany(mappedBy = "person", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<ReadingProgress> readingProgress;

    @OneToMany(mappedBy = "person", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SearchHistory> searchHistory;

    @ManyToOne
    @JoinColumn(name = "publisher_id")
    @JsonBackReference
    private Publisher publisher;

    public void setPublisher(Publisher publisher) {
        // Запобігання нескінченному циклу
        if (this.publisher == publisher) return;

        // Зберігаємо старого видавця
        Publisher oldPublisher = this.publisher;

        // Встановлюємо нового видавця
        this.publisher = publisher;

        // Видаляємо з попереднього видавця
        if (oldPublisher != null) {
            oldPublisher.removePerson(this);
        }

        // Додаємо себе до нового видавця
        if (publisher != null) {
            publisher.addPerson(this);
        }
    }

//    public void setPublisher(Publisher publisher) {
//        this.publisher = publisher;
//        if (!publisher.getPersons().contains(this)) {
//            publisher.getPersons().add(this);
//        }
//    }


}

