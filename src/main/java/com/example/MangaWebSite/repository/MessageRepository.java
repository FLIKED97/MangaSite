package com.example.MangaWebSite.repository;

import com.example.MangaWebSite.models.Message;
import com.example.MangaWebSite.models.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Integer> {

    List<Message> findBySenderAndReceiverOrReceiverAndSenderOrderByCreatedAtAsc(
            Person sender1, Person receiver1, Person sender2, Person receiver2);
}

