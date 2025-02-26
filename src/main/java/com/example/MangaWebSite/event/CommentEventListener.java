package com.example.MangaWebSite.event;

import com.example.MangaWebSite.models.UserProfile;
import com.example.MangaWebSite.repository.UserProfileRepository;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class CommentEventListener {

    private final UserProfileRepository userProfileRepository;

    public CommentEventListener(UserProfileRepository userProfileRepository) {
        this.userProfileRepository = userProfileRepository;
    }

    @EventListener
    @Transactional
    public void handleCommentCreated(CommentCreatedEvent event) {
        // Отримуємо користувача з події
        var person = event.getPerson();

        // Завантажуємо профіль користувача (або створюємо новий, якщо не існує)
        UserProfile profile = userProfileRepository.findById(person.getId())
                .orElseGet(() -> {
                    UserProfile newProfile = new UserProfile();
                    newProfile.setPerson(person);
                    return newProfile;
                });

        // Незалежно від типу коментаря збільшуємо загальний лічильник коментарів
        profile.setCommentsPosted(profile.getCommentsPosted() + 1);

        // (Опціонально) Можна враховувати тип коментаря окремо, наприклад, окремі поля для коміксів і новин

        // Зберігаємо оновлений профіль
        userProfileRepository.save(profile);
    }
}
