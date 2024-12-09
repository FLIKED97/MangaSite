package com.example.MangaWebSite.repository;

import com.example.MangaWebSite.models.FriendRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FriendRequestRepository extends JpaRepository<FriendRequest, Integer> {

    // Отримати всі запити, отримані користувачем
    List<FriendRequest> findByRecipientId(int recipientId);

    // Отримати всі запити, надіслані користувачем
    List<FriendRequest> findBySenderId(int senderId);
}
