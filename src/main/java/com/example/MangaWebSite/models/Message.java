package com.example.MangaWebSite.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "messages")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "sender_id", nullable = false)
    private Person sender; // Відправник повідомлення

    @ManyToOne
    @JoinColumn(name = "receiver_id", nullable = false)
    private Person receiver; // Отримувач повідомлення

    @Column(name = "content", columnDefinition = "TEXT", nullable = false)
    private String content; // Текст повідомлення

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MessageStatus status; // SENT, DELIVERED, READ

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}

