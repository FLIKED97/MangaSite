package com.example.MangaWebSite.service;

import com.example.MangaWebSite.models.Message;
import com.example.MangaWebSite.models.MessageStatus;
import com.example.MangaWebSite.models.Person;
import com.example.MangaWebSite.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class MessageService {

    private final MessageRepository messageRepository;

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
}
