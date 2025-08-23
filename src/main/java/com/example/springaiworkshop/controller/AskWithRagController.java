package com.example.springaiworkshop.controller;

import com.example.springaiworkshop.model.Answer;
import com.example.springaiworkshop.model.Question;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.vectorstore.qdrant.QdrantVectorStore;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AskWithRagController {

    private final ChatClient chatClient;

    public AskWithRagController(ChatClient.Builder clientBuilder, QdrantVectorStore vectorStore) {
        this.chatClient = clientBuilder
            .defaultAdvisors(
                // Vector store context retrieval advisor provided by Spring AI:
                QuestionAnswerAdvisor.builder(vectorStore).build()
            )
            .build();
    }

    @PostMapping("/ask_rag")
    public Answer askRag(@RequestBody Question question) {
        return chatClient
            .prompt(question.question())
            .call()
            .entity(Answer.class);
    }

}
