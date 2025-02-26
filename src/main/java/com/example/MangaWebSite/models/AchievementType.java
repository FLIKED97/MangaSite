package com.example.MangaWebSite.models;

import java.util.function.Function;

public enum AchievementType {
    COMICS_READ(UserProfile::getComicsRead),
    CHAPTERS_READ(UserProfile::getChaptersRead),
    COMMENTS(UserProfile::getCommentsPosted),
    LIKES_RECEIVED(UserProfile::getLikesReceived),
    DAYS_STREAK(UserProfile::getDaysStreak),
    COLLECTIONS_CREATED(UserProfile::getCollectionsCreated);

    private final Function<UserProfile, Integer> valueExtractor;

    AchievementType(Function<UserProfile, Integer> valueExtractor) {
        this.valueExtractor = valueExtractor;
    }

    public int extractValue(UserProfile profile) {
        return valueExtractor.apply(profile);
    }
}