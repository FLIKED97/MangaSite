package com.example.MangaWebSite.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "pages")
public class ComicPage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chapter_id", nullable = false)
    private Chapter chapter;

    @Column(name = "image_path", nullable = false)
    private String imagePath;

    @Column(name = "page_number", nullable = false)
    private int pageNumber;

    @Override
    public String toString() {
        return "ComicPage{" +
                "id=" + id +
                ", pageNumber=" + pageNumber +
                ", imagePath='" + imagePath + '\'' +
                '}';
    }
}

