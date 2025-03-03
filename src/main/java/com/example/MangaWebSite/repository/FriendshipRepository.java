package com.example.MangaWebSite.repository;

import com.example.MangaWebSite.models.Friendship;
import com.example.MangaWebSite.models.FriendshipStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FriendshipRepository extends JpaRepository<Friendship, Integer> {

    List<Friendship> findByPersonIdAndStatus(int personId, FriendshipStatus status);

    List<Friendship> findByFriendIdAndStatus(int friendId, FriendshipStatus status);

    @Query("SELECT f FROM Friendship f WHERE (f.person.id = :personId AND f.friend.id = :friendId) OR (f.person.id = :friendId AND f.friend.id = :personId)")
    Optional<Friendship> findFriendshipBetweenUsers(@Param("personId") int personId, @Param("friendId") int friendId);

    @Query("SELECT f FROM Friendship f WHERE (f.person.id = :userId OR f.friend.id = :userId) AND f.status = :status")
    List<Friendship> findAllByPersonIdAndStatus(@Param("userId") int userId, @Param("status") FriendshipStatus status);
}

