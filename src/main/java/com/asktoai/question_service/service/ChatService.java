package com.asktoai.question_service.service;

import com.asktoai.question_service.dto.Answer;
import com.asktoai.question_service.utils.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class ChatService {

    private final DbService dbService;

    public ChatService(DbService dbService) {
        this.dbService = dbService;
    }

    public Answer getAnswerFromGemini(String question) {

        var answer = fetchAnswerFromAI(question);

        answer.thenAccept( v -> {
            log.info("Saving answer to DB" + v);
        });

        return answer.join();
    }

    public CompletableFuture<Answer> fetchAnswerFromAI(String question) {
        var answer = new CompletableFuture<Answer>();

        Thread.ofVirtual()
                .name("VThread-", Thread.currentThread().threadId())
                .start(() -> {
            log.info("Getting answer from Gemini");
            CommonUtils.sleep(Duration.ofSeconds(3));
            answer.complete(new Answer(1, question, "Here is your answer to your question"));
        });

        return answer;
    }
}
