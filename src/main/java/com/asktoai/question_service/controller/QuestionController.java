package com.asktoai.question_service.controller;

import com.asktoai.question_service.dto.Answer;
import com.asktoai.question_service.dto.Question;
import com.asktoai.question_service.service.ChatService;
import com.asktoai.question_service.service.VirtualThreadTestService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/askmeanything")
public class QuestionController {

    private final ChatService chatService;

    private final VirtualThreadTestService virtualThreadTestService;

    public QuestionController(ChatService chatService, VirtualThreadTestService virtualThreadTestService) {
        this.chatService = chatService;
        this.virtualThreadTestService = virtualThreadTestService;
    }

    @PostMapping(value = "/question/virtualthread")
    public ResponseEntity<Answer> askQuestionToVirtualThread(
            @RequestBody String question
    ) throws ExecutionException, InterruptedException {
        return ResponseEntity.ok(virtualThreadTestService.getAnswerFromDelayedVirtualThread());
    }

    @PostMapping(value = "/question/openai")
    public ResponseEntity<Answer> askQuestiontoOpenAI(
            @RequestBody Question question
    ){
        return ResponseEntity.ok(chatService.getAnswersFromOpenAi(question));
    }




}
