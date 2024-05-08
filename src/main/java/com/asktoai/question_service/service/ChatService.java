package com.asktoai.question_service.service;

import com.asktoai.question_service.dto.Answer;
import com.asktoai.question_service.utils.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class ChatService {

    private final DbService dbService;

    public ChatService(DbService dbService) {
        this.dbService = dbService;
    }

    public Answer getAnswerFromGemini(String question) throws ExecutionException, InterruptedException {

        var answer = supplyAnswerFromAI(question);

        answer.thenAccept(result -> log.info("Fetched following answer from AI \n" + result.answerText()));
        CommonUtils.sleep(Duration.ofSeconds(4));
        return answer.get();
    }


    /**
     * This method is used to fetch the answer from AI using CompletableFuture with supplyAsync.
     * This is more elegant way to create CompletableFuture.
     * Burada daha iyi bir örnek olusturmak icin supplyAsync kullanilarak verilmistir.
     * Asagidaki methodda (fetchAnswerFromAIWithBasicCompletableFuture) sadece simple CompletableFuture kullanilarak verilmistir.
     * @return CompletableFuture<Answer>
     */
    public CompletableFuture<Answer> supplyAnswerFromAI(String question) {

        var executor = Executors.newVirtualThreadPerTaskExecutor();

        return CompletableFuture.supplyAsync(() -> {
            log.info("Getting answer from Gemini");
            CommonUtils.sleep(Duration.ofSeconds(2));
            return new Answer(1, question, "Here is your answer to your question");
        }, executor)
                .exceptionally(ex -> {
                    log.error("Error occurred while fetching answer from AI", ex);
                    return new Answer(1, question, "Sorry, I am unable to fetch the answer at the moment");
        })      .orTimeout(10, TimeUnit.SECONDS)
                .exceptionally(ex -> {
                    log.error("Timeout occurred while fetching answer from AI", ex);
                    return new Answer(1, question, "Sorry, I am unable to fetch the answer at the moment. Reason: timeout occurred");
                });
    }

    /**
     * This method is used to fetch the answer from AI using Simple CompletableFuture
     * Bu sadece complatable Future kullanilarak verilmis bir örnek method.
     * Diger method da (supplyAnswerFromAI) supplyAsync kullanilarak daha iyi bir örnek verilmistir.
     * @return CompletableFuture<Answer>
     */
    public CompletableFuture<Answer> fetchAnswerFromAIWithBasicCompletableFuture(String question) {
        var answer = new CompletableFuture<Answer>();

        Thread.ofVirtual()
                .name("VThread-", Thread.currentThread().threadId())
                .start(() -> {
                    log.info("Getting answer from Gemini");
                    CommonUtils.sleep(Duration.ofSeconds(2));
                    answer.complete(new Answer(1, question, "Here is your answer to your question"));
                });

        return answer;
    }
}
