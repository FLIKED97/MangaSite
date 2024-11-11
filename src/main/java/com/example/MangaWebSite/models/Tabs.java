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
@Table(name = "tabs")
public class Tabs {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "name", nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "person_id", nullable = false)
    private Person person;

    @ManyToMany
    @JoinTable(
            name = "tabs_comics", // Назва таблиці для зв'язку
            joinColumns = @JoinColumn(name = "tabs_id"),
            inverseJoinColumns = @JoinColumn(name = "comics_id")
    )
    private List<Comics> comics;  // Багато коміксів можуть бути в одній закладці

    @Column(name = "added_at", nullable = false)
    private LocalDate addedAt;
}


