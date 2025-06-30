package com.example.MangaWebSite;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MangaWebSiteApplication {

	public static void main(String[] args) {
		SpringApplication.run(MangaWebSiteApplication.class, args);
	}

}
