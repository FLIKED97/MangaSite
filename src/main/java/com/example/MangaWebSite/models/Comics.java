package com.example.MangaWebSite.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
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
    private LocalDate createdAt;

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

    @OneToMany(mappedBy = "comics", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Rating> ratings; //TODO Реалізувати присвоєння рейтинга коміксу

    // Метод для обчислення середньої оцінки
    public double getAverageRating() {
        return ratings.stream()
                .mapToInt(Rating::getRating)
                .average()
                .orElse(0.0);
    }

    // Метод для зберігання кількості переглядів
    @Column(name = "view_count", nullable = false)
    private int viewCount = 0;

    public void incrementViewCount() {
        this.viewCount++;
    }

    @Column(name = "popularity_rating")
    private double popularityRating = 1.0;

    public void calculatePopularityRating() {
        double viewScore = this.viewCount * 0.5;  // 0.5 балів за кожен перегляд
        double ratingScore = getAverageRating() * 2;  // середня оцінка * 2
        double commentScore = this.comments.size() * 1.0;  // 1 бал за кожен коментар

        this.popularityRating = viewScore + ratingScore + commentScore;
    }


}
