package com.example.MangaWebSite.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Data
@NoArgsConstructor
@Entity
@Table(name = "reading_progress", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"person_id", "chapter_id"})
})
@JsonIgnoreProperties({"chapter", "person", "comics"})
public class ReadingProgress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @ManyToOne
    @JoinColumn(name = "person_id", nullable = false)
    private Person person;

    @ManyToOne
    @JoinColumn(name = "chapter_id", nullable = false)
    private Chapter chapter;

    @Column(name = "last_page", nullable = false)
    private int lastPage;

    @Column(name = "is_completed")
    private boolean isCompleted = false;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "last_interaction")
    private LocalDateTime lastInteraction;

    @PrePersist
    @PreUpdate
    public void updateTimestamp() {
        this.updatedAt = LocalDateTime.now();
    }
}

