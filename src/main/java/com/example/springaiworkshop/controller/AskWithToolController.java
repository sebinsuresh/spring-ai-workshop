package com.example.springaiworkshop.controller;

import com.example.springaiworkshop.model.Answer;
import com.example.springaiworkshop.model.Question;
import com.example.springaiworkshop.tool.WeatherTools;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AskWithToolController {

    private final ChatClient client;

    public AskWithToolController(ChatClient.Builder builder, WeatherTools weatherTools) {
        this.client = builder
            // Add weather tool - Can also add this at call time:
            .defaultTools(weatherTools)
            .build();
    }

    @PostMapping("/ask_tool")
    public Answer askTool(@RequestBody Question question) {
        return this.client
            .prompt()
            .user(question.question())
            .call()
            .entity(Answer.class);
    }
}
