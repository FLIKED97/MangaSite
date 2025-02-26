package com.example.MangaWebSite.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_profile")
@Data
@NoArgsConstructor
public class UserProfile {
    @Id
    @Column(name = "person_id")
    private int personId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "person_id")
    private Person person;

    @Column(name = "experience_points", nullable = false)
    private int experiencePoints = 0;

    @Column(name = "level", nullable = false)
    private int level = 1;

    @Column(name = "comics_read")
    private int comicsRead = 0;

    @Column(name = "chapters_read")
    private int chaptersRead = 0;

    @Column(name = "comments_posted")
    private int commentsPosted = 0;

    @Column(name = "likes_received")
    private int likesReceived = 0;

    @Column(name = "days_streak")
    private int daysStreak = 0;

    @Column(name = "collections_created")
    private int collectionsCreated = 0;

    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;

    @PrePersist
    @PreUpdate
    public void updateTimestamp() {
        this.lastUpdated = LocalDateTime.now();
    }
}