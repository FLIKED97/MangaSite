package com.example.MangaWebSite.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@Entity
@Table(name = "chapters", uniqueConstraints = @UniqueConstraint(columnNames = {"comic_id", "chapter_number"}))
public class Chapter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
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

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "release_date")
    private LocalDate releaseDate;
    @OneToMany(mappedBy = "chapter", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Page> pages = new ArrayList<>();

    // Метод для додавання сторінки
    public void addPage(Page page) {
        pages.add(page);
        page.setChapter(this); // Встановлюємо зв'язок сторінки з главою
    }
//    @OneToMany(mappedBy = "chapter", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<Page> pages = new ArrayList<>();

//    public void addPage(Page page) {
//        if (pages == null) {
//            pages = new ArrayList<>();
//        }
//        pages.add(page);
//        page.setChapter(this); // Встановлюємо зворотне посилання на главу
//    }
}
