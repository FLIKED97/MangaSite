package com.example.MangaWebSite.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ComicsDTO {
    private Integer id;
    private String title;
    private String comicsTypeDisplay;


    // Getters and setters
}
