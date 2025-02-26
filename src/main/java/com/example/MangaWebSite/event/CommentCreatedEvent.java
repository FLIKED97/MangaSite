package com.example.MangaWebSite.event;

import com.example.MangaWebSite.models.Person;
import org.springframework.context.ApplicationEvent;

public class CommentCreatedEvent extends ApplicationEvent {

    // Тип коментаря: "comics" або "news"
    private final String commentType;

    // Користувач, який створив коментар
    private final Person person;

    public CommentCreatedEvent(Object source, Person person, String commentType) {
        super(source);
        this.person = person;
        this.commentType = commentType;
    }

    public Person getPerson() {
        return person;
    }

    public String getCommentType() {
        return commentType;
    }
}
