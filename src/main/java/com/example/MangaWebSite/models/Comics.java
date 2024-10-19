package com.example.MangaWebSite.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@Entity
@Table(name = "comics")
public class Comics {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "author", nullable = false)
    private String author;

    @Lob // (BLOB)
    @Column(name = "cover_image", nullable = true)
    private byte[] coverImage;

    @Column(name = "image_type", nullable = true)
    private String imageType;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "comics_genres", // Проміжна таблиця
            joinColumns = @JoinColumn(name = "comics_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id")
    )
    private List<Genre> genres;  // Зв'язок "багато до багатьох" з жанрами

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "created_at", nullable = false)
    private Date createdAt;

    @OneToMany(mappedBy = "comics", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Chapter> chapters;

    @OneToMany(mappedBy = "comics", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments;

    @OneToMany(mappedBy = "comics", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReadingProgress> readingProgress;

    @ManyToMany(mappedBy = "comics")
    private List<Tabs> tabs;  // Комікси можуть бути в багатьох закладках

    @OneToMany(mappedBy = "comics", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Statistics> statistics;
}
