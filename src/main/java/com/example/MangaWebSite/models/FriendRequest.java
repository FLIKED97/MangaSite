package com.example.MangaWebSite.models;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "friend_requests")
public class FriendRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "sender_id", nullable = false)
    private Person sender; // Хто відправив запит

    @ManyToOne
    @JoinColumn(name = "recipient_id", nullable = false)
    private Person recipient; // Хто отримав запит

    @Column(name = "status", nullable = false)
    private String status; // Статус запиту: "PENDING", "ACCEPTED", "DECLINED"
}
