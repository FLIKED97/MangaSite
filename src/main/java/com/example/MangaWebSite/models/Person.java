package com.example.MangaWebSite.models;

import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
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

    @OneToMany(mappedBy = "person", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Friendship> friendshipsInitiated; // Ініційовані користувачем дружби

    @OneToMany(mappedBy = "friend", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Friendship> friendshipsReceived; // Запити дружби, отримані користувачем


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
    @Lob
    @JsonIgnore
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "avatar")
    private byte[] avatar;

    @Column(name = "avatar_content_type")
    private String avatarContentType;

    public long getDaysInApp() {
        return ChronoUnit.DAYS.between(createdAt, LocalDateTime.now());
    }
    public boolean hasAvatar() {
        return avatar != null && avatar.length > 0;
    }
}

