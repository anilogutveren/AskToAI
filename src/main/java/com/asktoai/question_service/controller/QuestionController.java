package com.asktoai.question_service.controller;

import com.asktoai.question_service.dto.Answer;
import com.asktoai.question_service.service.ChatService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/askmeanything")
public class QuestionController {

    private final ChatService chatService;

    public QuestionController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping(value = "/question")
    public ResponseEntity<Answer> askQuestion(
            @RequestBody String question
    ) {
        return ResponseEntity.ok(chatService.getAnswerFromGemini(question));
    }

}
