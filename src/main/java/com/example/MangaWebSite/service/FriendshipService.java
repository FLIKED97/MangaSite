package com.example.MangaWebSite.service;

import com.example.MangaWebSite.models.Friendship;
import com.example.MangaWebSite.models.FriendshipStatus;
import com.example.MangaWebSite.models.Person;
import com.example.MangaWebSite.repository.FriendshipRepository;
import com.example.MangaWebSite.repository.PersonRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Transactional
public class FriendshipService {

    private final FriendshipRepository friendshipRepository;
    private final PersonRepository personRepository;

    // Відправити запит на дружбу
    public void createFriendRequest(int senderId, int recipientId) {
        if (areFriends(senderId, recipientId)) {
            throw new IllegalStateException("Ви вже друзі.");
        }

        Person sender = personRepository.findById(senderId)
                .orElseThrow(() -> new IllegalArgumentException("Користувач не знайдений"));
        Person recipient = personRepository.findById(recipientId)
                .orElseThrow(() -> new IllegalArgumentException("Друг не знайдений"));

        Friendship friendship = new Friendship();
        friendship.setPerson(sender);
        friendship.setFriend(recipient);
        friendship.setStatus(FriendshipStatus.PENDING);
        friendshipRepository.save(friendship);
    }

    // Підтвердити запит на дружбу
    public void acceptFriendRequest(int friendshipId) {
        Friendship friendship = friendshipRepository.findById(friendshipId)
                .orElseThrow(() -> new IllegalArgumentException("Запит не знайдено"));

        friendship.setStatus(FriendshipStatus.ACCEPTED);
        friendshipRepository.save(friendship);
    }

    // Відхилити запит на дружбу
    public void declineFriendRequest(int friendshipId) {
        Friendship friendship = friendshipRepository.findById(friendshipId)
                .orElseThrow(() -> new IllegalArgumentException("Запит не знайдено"));

        friendship.setStatus(FriendshipStatus.DECLINED);
        friendshipRepository.save(friendship);
    }

    // Отримати список друзів (підтверджена дружба)
    @Transactional
    public List<Person> getFriends(int userId) {
        List<Friendship> friendships = friendshipRepository.findAllByPersonIdAndStatus(userId, FriendshipStatus.ACCEPTED);

        return friendships.stream()
                .map(friendship -> friendship.getPerson().getId() == userId ? friendship.getFriend() : friendship.getPerson())
                .collect(Collectors.toList());
    }


    // Отримати вхідні запити
    public List<Friendship> getReceivedFriendRequests(int userId) {
        return friendshipRepository.findByFriendIdAndStatus(userId, FriendshipStatus.PENDING);
    }

    // Отримати вихідні запити
    public List<Friendship> getSentFriendRequests(int userId) {
        return friendshipRepository.findByPersonIdAndStatus(userId, FriendshipStatus.PENDING);
    }

    public boolean areFriends(int userId, int friendId) {
        return friendshipRepository.findFriendshipBetweenUsers(userId, friendId)
                .map(f -> f.getStatus() == FriendshipStatus.ACCEPTED)
                .orElse(false);
    }

    public Person getPersonById(int friendId) {
        return personRepository.findById(friendId).orElse(null);
    }
}


