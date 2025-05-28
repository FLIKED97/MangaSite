package com.example.MangaWebSite.models;

import com.example.MangaWebSite.serializer.ComicsSerializer;
import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Formula;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;

@Data
@NoArgsConstructor
@Entity
@Table(name = "comics")
@JsonSerialize(using = ComicsSerializer.class)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Comics {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "original_title")
    private String originalTitle;

    @Column(name = "author", nullable = false)
    private String author;

    @Lob // (BLOB)
    @JsonIgnore // Додаємо цю анотацію
    @Basic(fetch = FetchType.EAGER)
    @Column(name = "cover_image", nullable = true, length = 100000)
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
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime createdAt;

    @Column(name = "published_at")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate publishedAt;

    @Formula("(SELECT COALESCE(AVG(r.rating), 0) FROM ratings r WHERE r.comic_id = id)")
    private double averageRating;

    @JsonIgnoreProperties({"comics", "comicPages"})
    @OneToMany(mappedBy = "comics", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Chapter> chapters;

    @JsonIgnore
    @OneToMany(mappedBy = "comics", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments;

    @JsonIgnore
    @JsonIgnoreProperties({"person"}) // щоб уникнути циклічних посилань
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
    @Column(name = "comics_type")
    @Enumerated(EnumType.STRING)
    private ComicsType comicsType;

    @Column(name = "publication_type")
    @Enumerated(EnumType.STRING)
    private PublicationType publicationType = PublicationType.COMIC; // за замовчуванням - комікс

    // Для збереження файлу книги (якщо застосовується)
    @Column(name = "book_file_path")
    private String bookFilePath;

    @Lob
    @Column(name = "description_embedding")
    private byte[] descriptionEmbedding;

    public ComicsType getComicsType() {
        return comicsType;
    }

    public void setComicsType(ComicsType comicsType) {
        this.comicsType = comicsType;
    }

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
    @JsonIgnore
    public String getCoverImageBase64() {
        if (coverImage == null) return null;
        return Base64.getEncoder().encodeToString(coverImage);
    }
    public int getTotalPagesForChapter(Chapter chapter) {
        return chapter.getComicPages().size();
    }
    public LocalDate getPublishedAt() {
        return publishedAt;
    }
    public byte[] getSafeCoverImage() {
        return (coverImage != null && coverImage.length > 0) ? coverImage : null;
    }

    public static byte[] serializeFloatArray(float[] array) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        for (float f : array) {
            dos.writeFloat(f);
        }
        return baos.toByteArray();
    }

    public static float[] deserializeFloatArray(byte[] bytes) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        DataInputStream dis = new DataInputStream(bais);
        int floatCount = bytes.length / 4;
        float[] array = new float[floatCount];
        for (int i = 0; i < floatCount; i++) {
            array[i] = dis.readFloat();
        }
        return array;
    }
    public static double computeCosineSimilarity(float[] vec1, float[] vec2) {
        double dotProduct = 0.0;
        double norm1 = 0.0;
        double norm2 = 0.0;
        for (int i = 0; i < vec1.length; i++) {
            dotProduct += vec1[i] * vec2[i];
            norm1 += vec1[i] * vec1[i];
            norm2 += vec2[i] * vec2[i];
        }
        return dotProduct / (Math.sqrt(norm1) * Math.sqrt(norm2));
    }


}
