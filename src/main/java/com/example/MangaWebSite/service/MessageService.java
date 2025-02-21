package com.example.MangaWebSite.service;

import com.example.MangaWebSite.exception.ResourceNotFoundException;
import com.example.MangaWebSite.models.*;
import com.example.MangaWebSite.repository.ComicsRepository;
import com.example.MangaWebSite.repository.MessageRepository;
import com.example.MangaWebSite.repository.PersonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class MessageService {

    private final MessageRepository messageRepository;
    private final ComicsRepository comicsRepository;
    private final PersonRepository personRepository;

    public List<Message> getChatMessages(Person sender, Person receiver) {
        return messageRepository.findBySenderAndReceiverOrReceiverAndSenderOrderByCreatedAtAsc(sender, receiver, sender, receiver);
    }

    public Message sendMessage(Person sender, Person receiver, String content) {
        Message message = new Message();
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setContent(content);
        message.setStatus(MessageStatus.SENT);
        return messageRepository.save(message);
    }

    public void updateMessageStatus(int messageId, MessageStatus status) {
        Message message = messageRepository.findById(messageId).orElseThrow();
        message.setStatus(status);
        messageRepository.save(message);
    }
    public Message save(Message message) {
        return messageRepository.save(message);
    }
//    TODO ЗРОБИТИ СВІЙ ВАРІАНТ
    public List<Message> shareComicsWithFriends(
            int comicsId,
            List<Integer> friendIds,
            Person sender) {

        Comics comics = comicsRepository.findById(comicsId)
                .orElseThrow(() -> new ResourceNotFoundException("Comics not found"));

        List<Message> messages = new ArrayList<>();

        for (Integer friendId : friendIds) {
            Person receiver = personRepository.findById(friendId)
                    .orElseThrow(() -> new ResourceNotFoundException("Friend not found"));

            Message message = new Message();
            message.setSender(sender);
            message.setReceiver(receiver);
            message.setComics(comics);
            message.setMessageType(MessageType.COMICS_SHARE);
            message.setContent(generateComicsShareMessage(comics));
            message.setStatus(MessageStatus.SENT);

            messages.add(save(message));
        }

        return messages;
    }

    private String generateComicsShareMessage(Comics comics) {
        return String.format("""
            🎬 Shared Comics:
            Title: %s
            Rating: %.1f
            Status: %s
            """,
                comics.getTitle(),
                comics.getAverageRating(),
                comics.getStatus()
        );
    }
}
