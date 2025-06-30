package com.example.MangaWebSite.controller;

import com.example.MangaWebSite.models.Comics;
import com.example.MangaWebSite.service.RecommendationService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@AllArgsConstructor
@RequestMapping("/api")
public class ChatRecomController {

    private final RecommendationService recommendationService;

    @GetMapping("/chat")
    public String getChat(Model model) {
        model.addAttribute("messages", new ArrayList<>());
        return "chat";
    }

    @PostMapping("/chat/send")
    public String sendMessage(@RequestParam("userMessage") String userMessage, Model model) throws Exception {
        List<Comics> recommendations = recommendationService.findSimilarComics(userMessage, 1);
        StringBuilder assistantReply = new StringBuilder("Рекомендовані книги/комікси:<br/>");
        for (Comics rec : recommendations) {
            assistantReply.append(rec.getTitle()).append("<br/>");
        }
        List<Map<String, String>> messages = new ArrayList<>();
        Map<String, String> userMsg = new HashMap<>();
        userMsg.put("sender", "Ви");
        userMsg.put("text", userMessage);
        messages.add(userMsg);
        Map<String, String> assistantMsg = new HashMap<>();
        assistantMsg.put("sender", "Помічник");
        assistantMsg.put("text", assistantReply.toString());
        messages.add(assistantMsg);
        model.addAttribute("messages", messages);
        return "chat";
    }
}
