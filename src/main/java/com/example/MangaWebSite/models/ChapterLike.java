package com.example.MangaWebSite.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "chapter_likes")
@Data
@NoArgsConstructor
public class ChapterLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    // Зв'язок з главою (Chapter)
    @ManyToOne(optional = false)
    @JoinColumn(name = "chapter_id", nullable = false)
    private Chapter chapter;

    // Зв'язок з користувачем, який поставив лайк (Person)
    @ManyToOne(optional = false)
    @JoinColumn(name = "person_id", nullable = false)
    private Person person;

    // Дата та час, коли поставлено лайк (опціонально)
    @Column(name = "liked_at", nullable = false)
    private LocalDateTime likedAt = LocalDateTime.now();
}