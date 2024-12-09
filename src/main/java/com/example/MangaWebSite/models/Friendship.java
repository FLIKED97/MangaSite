package com.example.MangaWebSite.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "friendships")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Friendship {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "person_id", nullable = false)
    private Person person; // Користувач, який ініціює дружбу

    @ManyToOne
    @JoinColumn(name = "friend_id", nullable = false)
    private Person friend; // Друг користувача

    @Enumerated(EnumType.STRING)
    private FriendshipStatus status; // Статус дружби (наприклад, запит, підтверджено)

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Конструктор для створення нової дружби без id
    public Friendship(Person person, Person friend, FriendshipStatus status, LocalDateTime createdAt) {
        this.person = person;
        this.friend = friend;
        this.status = status;
        this.createdAt = createdAt;
    }

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}

