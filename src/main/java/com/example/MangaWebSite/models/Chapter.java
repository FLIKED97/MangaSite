package com.example.MangaWebSite.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@Entity
@Table(name = "chapters", uniqueConstraints = @UniqueConstraint(columnNames = {"comic_id", "chapter_number"}))
public class Chapter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @JsonIgnoreProperties("chapters")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comic_id", nullable = false)
    private Comics comics;

    @JsonIgnoreProperties("chapters")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "publisher_id", nullable = false)
    private Publisher publisher;

    @Column(name = "chapter_number", nullable = false)
    private int chapterNumber;

    @Column(name = "title")
    private String title;

    @Column(name = "pages_count", nullable = false)
    private int pagesCount;

    @Column(name = "thanks_count")
    private Integer thanksCount = 0;

    @Column(name = "volume") // Додано поле для тому
    private int volume;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "release_date")
    private LocalDateTime releaseDate;

    @JsonIgnoreProperties("chapter")
    @OneToMany(mappedBy = "chapter", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("pageNumber ASC")
    private List<ComicPage> comicPages = new ArrayList<>();

    // Метод для додавання сторінки
    public void addPage(ComicPage comicPage) {
        comicPages.add(comicPage);
        comicPage.setChapter(this); // Встановлюємо зв'язок сторінки з главою
    }
}
