package com.example.MangaWebSite.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@Entity
@Table(name = "chapters", uniqueConstraints = @UniqueConstraint(columnNames = {"comic_id", "chapter_number"}))
public class Chapter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "comic_id", nullable = false)
    private Comics comics;

    @ManyToOne
    @JoinColumn(name = "publisher_id", nullable = false)
    private Publisher publisher;

    @Column(name = "chapter_number", nullable = false)
    private int chapterNumber;

    @Column(name = "title")
    private String title;

    @Column(name = "pages_count", nullable = false)
    private int pagesCount;

    @Column(name = "release_date", nullable = false)
    private Date releaseDate;
}
