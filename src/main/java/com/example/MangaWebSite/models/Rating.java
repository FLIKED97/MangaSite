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
@Table(name = "ratings")
public class Rating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @ManyToOne
    @JoinColumn(name = "comic_id", nullable = false)
    private Comics comics;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Person person;

    @Column(name = "rating", nullable = false)
    private int rating; // Оцінка, наприклад, від 1 до 5

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}
