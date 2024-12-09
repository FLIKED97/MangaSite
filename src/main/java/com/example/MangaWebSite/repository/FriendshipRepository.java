package com.example.MangaWebSite.repository;

import com.example.MangaWebSite.models.Friendship;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FriendshipRepository extends JpaRepository<Friendship, Integer> {
    Optional<Object> findByPersonIdAndFriendId(int userId, int friendId);

    List<Friendship> findFriendsByPersonId(int userId);

    @Query("SELECT f FROM Friendship f WHERE (f.person.id = :personId AND f.friend.id = :friendId) OR (f.person.id = :friendId AND f.friend.id = :personId)")
    Optional<Friendship> findFriendshipBetweenUsers(@Param("personId") int personId, @Param("friendId") int friendId);

}
