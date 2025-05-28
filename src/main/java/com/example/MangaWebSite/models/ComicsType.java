package com.example.MangaWebSite.models;

public enum ComicsType {
    MANHWA("Манхва"),
    MANGA("Манґа"),
    COMICS("Комікс"),
    MANHUA("Маньхва"),
    BOOKS("Книга");

    private final String displayName;

    ComicsType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}