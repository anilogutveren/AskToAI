package com.asktoai.question_service.service;

import com.asktoai.question_service.dto.Answer;
import com.asktoai.question_service.dto.Question;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class ChatService {

    private final ChatClient chatClient;

    private final DbService dbService;

    public ChatService(@Qualifier("openAiChatClient") ChatClient chatClient, DbService dbService) {
        this.chatClient = chatClient;
        this.dbService = dbService;
    }

    /**
     * Fetching the answer from OpenAI
     */
    public Answer getAnswersFromOpenAi(Question question) {
        log.info("Fetching english answer from Open AI");

        PromptTemplate promptTemplateEnglish = new PromptTemplate(question.question() + " in English");
        Prompt promptEnglish = promptTemplateEnglish.create();

        PromptTemplate promptTemplateTurkish = new PromptTemplate(question.question() + " in Turkish");
        Prompt promptTurkish = promptTemplateTurkish.create();

        var combinedAnswer = new Answer("");

        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            var answerInEnglish = fetchAnswer(promptEnglish, executor);
            var answerInTurkish = fetchAnswer(promptTurkish, executor);

            combinedAnswer = answerInEnglish.thenCombine(answerInTurkish, (answer1, answer2) -> {
                return new Answer(answer1.answerText() + " \n" + answer2.answerText());
            }).join();

            log.info(String.valueOf(combinedAnswer));
        }

        return combinedAnswer;
    }

    private CompletableFuture<Answer> fetchAnswer(Prompt prompt, Executor executor) {

        return CompletableFuture.supplyAsync(() -> {
                    log.info("Getting answer from Gemini with executor thread: ");
                    return new Answer(chatClient.call(prompt).getResult().getOutput().getContent());
                }, executor)
                .exceptionally(ex -> {
                    log.error("Error occurred while fetching answer from AI", ex);
                    return new Answer("Sorry, I am unable to fetch the answer at the moment");
                })
                .orTimeout(10, TimeUnit.SECONDS)
                .exceptionally(ex -> {
                    log.error("Timeout occurred while fetching answer from AI", ex);
                    return new Answer("Sorry, I am unable to fetch the answer at the moment. Reason: timeout occurred");
                });
    }

}
