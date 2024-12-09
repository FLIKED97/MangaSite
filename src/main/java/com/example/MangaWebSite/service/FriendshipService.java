package com.example.MangaWebSite.service;

import com.example.MangaWebSite.models.FriendRequest;
import com.example.MangaWebSite.models.Friendship;
import com.example.MangaWebSite.models.FriendshipStatus;
import com.example.MangaWebSite.models.Person;
import com.example.MangaWebSite.repository.FriendRequestRepository;
import com.example.MangaWebSite.repository.FriendshipRepository;
import com.example.MangaWebSite.repository.PersonRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class FriendshipService {

    private final FriendshipRepository friendshipRepository;
    private final PersonRepository userRepository; // Для отримання користувачів
    private final FriendRequestRepository friendRequestRepository;

    // Отримання запитів, отриманих користувачем
    public List<FriendRequest> getReceivedFriendRequests(int userId) {
        return friendRequestRepository.findByRecipientId(userId);
    }

    // Отримання запитів, надісланих користувачем
    public List<FriendRequest> getSentFriendRequests(int userId) {
        return friendRequestRepository.findBySenderId(userId);
    }


    // Відправити запит на дружбу
    public void sendFriendRequest(int userId, int friendId) {
        if (friendshipRepository.findFriendshipBetweenUsers(userId, friendId).isPresent()) {
            throw new IllegalStateException("Дружба вже існує!");
        }

        Person user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Користувач не знайдений"));
        Person friend = userRepository.findById(friendId)
                .orElseThrow(() -> new IllegalArgumentException("Друг не знайдений"));

        Friendship friendship = new Friendship(user, friend, FriendshipStatus.REQUESTED, LocalDateTime.now());
        friendshipRepository.save(friendship);
    }

    // Підтвердити запит на дружбу
    public void acceptFriendRequest(int requestId) {
        FriendRequest request = friendRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Запит не знайдено"));
        request.setStatus("ACCEPTED");
        friendRequestRepository.save(request);
    }

    // Відхилити запит на дружбу
    public void declineFriendRequest(int requestId) {
        FriendRequest request = friendRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Запит не знайдено"));
        request.setStatus("DECLINED");
        friendRequestRepository.save(request);
    }

    // Отримати список друзів користувача
    public List<Friendship> getFriends(int userId) {
        return friendshipRepository.findFriendsByPersonId(userId);
    }

    public boolean areFriends(int userId, int friendId) {
        return friendshipRepository.findFriendshipBetweenUsers(userId, friendId).isPresent();
    }

    public void createFriendRequest(Person sender, Person recipient) {
        FriendRequest request = new FriendRequest();
        request.setSender(sender);
        request.setRecipient(recipient);
        request.setStatus("PENDING");
        friendRequestRepository.save(request);
    }
}

