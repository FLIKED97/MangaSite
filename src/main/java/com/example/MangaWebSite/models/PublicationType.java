package com.example.MangaWebSite.models;

public enum PublicationType {
    COMIC("Комікс"),
    BOOK("Книга");

    private final String displayName;

    PublicationType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
