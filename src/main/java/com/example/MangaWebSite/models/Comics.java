package com.example.MangaWebSite.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Base64;
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
    @JsonIgnore // Додаємо цю анотацію
    @Basic(fetch = FetchType.LAZY)
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

    @JsonIgnoreProperties({"comics", "comicPages"})
    @OneToMany(mappedBy = "comics", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Chapter> chapters;
    @JsonIgnore
    @OneToMany(mappedBy = "comics", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments;

    @JsonIgnore
    @OneToMany(mappedBy = "comics", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReadingProgress> readingProgress;

    @JsonIgnore
    @ManyToMany(mappedBy = "comics")
    private List<Tabs> tabs;  // Комікси можуть бути в багатьох закладках
    @Column(name = "view_count", nullable = false)
    private int viewCount = 0;
    @JsonIgnore
    @OneToMany(mappedBy = "comics", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Statistics> statistics;

    @Column(name = "popularity_rating")
    private double popularityRating = 1.0;
    @JsonIgnore
    @OneToMany(mappedBy = "comics", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Rating> ratings;

    // Метод для обчислення середньої оцінки
    public double getAverageRating() {
        return ratings.stream()
                .mapToInt(Rating::getRating)
                .average()
                .orElse(0.0);
    }

    public void incrementViewCount() {
        this.viewCount++;
    }

    public void calculatePopularityRating() {
        double viewScore = this.viewCount * 0.5;  // 0.5 балів за кожен перегляд
        double ratingScore = getAverageRating() * 2;  // середня оцінка * 2
        double commentScore = this.comments.size() * 1.0;  // 1 бал за кожен коментар

        this.popularityRating = viewScore + ratingScore + commentScore;
    }
    @JsonProperty("coverImageBase64")
    public String getCoverImageBase64() {
        if (coverImage == null) return null;
        return Base64.getEncoder().encodeToString(coverImage);
    }

    public int getTotalPagesForChapter(Chapter chapter) {
        return chapter.getComicPages().size();
    }
}
