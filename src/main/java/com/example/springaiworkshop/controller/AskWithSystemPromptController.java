package com.example.springaiworkshop.controller;

import com.example.springaiworkshop.model.Answer;
import com.example.springaiworkshop.model.Question;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AskWithSystemPromptController {

    @Value("classpath:/promptTemplates/talkLike.st")
    Resource talkLIkeTemplateResource;

    private final ChatClient chatClient;

    public AskWithSystemPromptController(ChatClient.Builder clientBuilder) {
        // default chat client, no configuration:
        this.chatClient = clientBuilder.build();
    }

    @PostMapping("/ask_sys_prompt")
    public Answer askQuestion(@RequestBody Question question) {
        var prompt = chatClient.prompt()
            .user(question.question());

        // Give prompt as string:
        // prompt = prompt.system("Give nonsensical replies");

        // Give prompt from template file:
        prompt = prompt.system(systemSpec -> systemSpec
            .text(talkLIkeTemplateResource)
            .param("talkLike", question.talkLike()));

        // Get entity back instead of responseEntity (with metadata):
        // var entity = prompt
        //     .call()
        //     .entity(Answer.class);
        // return entity;

        var responseEntity = prompt
            .call()
            .responseEntity(Answer.class);

        var response = responseEntity.getResponse();

        if (response != null) {
            // Manual way to count tokens or get other metadata:

            var metadata = response.getMetadata();

            var totalTokens = metadata.getUsage().getTotalTokens();
            System.out.println("Total tokens: " + totalTokens);

            var model = metadata.getModel();
            System.out.println("Model: " + model);
        }

        return responseEntity.entity();
    }
}
