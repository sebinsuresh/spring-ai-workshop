package com.example.springaiworkshop.controller;

import com.example.springaiworkshop.model.Answer;
import com.example.springaiworkshop.model.Question;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AskWithMemoryController {

    private final ChatClient chatClient;

    public AskWithMemoryController(
        ChatClient.Builder clientBuilder
    ) {
        // this is the only ChatMemoryRepository implementation available right now:
        var chatMemoryRepository = new InMemoryChatMemoryRepository();

        // Uses a sliding window - can only do 50 or so:
        var messageWindowMemoryImpl = MessageWindowChatMemory
            .builder()
            .chatMemoryRepository(chatMemoryRepository)
            .build();

        // Advisor that keeps conversation history as a collection of messages - not all vendors support this.
        // Other options: PromptChatMemoryAdvisor, VectorStoreChatMemoryAdvisor
        // https://docs.spring.io/spring-ai/reference/api/chat-memory.html#_memory_in_chat_client
        var memoryAdvisor = MessageChatMemoryAdvisor
            .builder(messageWindowMemoryImpl)
            .build();

        this.chatClient = clientBuilder
            .defaultAdvisors(memoryAdvisor)
            .build();
    }

    @PostMapping("/ask_memory")
    public Answer askQuestion(
        @RequestBody Question question,
        @RequestHeader(
            name = "X-Conversation-Id",
            defaultValue = ChatMemory.DEFAULT_CONVERSATION_ID
        ) String conversationId
    ) {
        return chatClient.prompt()
            .user(question.question())
            .advisors(spec -> spec.param(ChatMemory.CONVERSATION_ID, conversationId))
            .call()
            .entity(Answer.class);
    }
}
