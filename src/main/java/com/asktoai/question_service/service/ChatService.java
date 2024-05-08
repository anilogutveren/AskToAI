package com.asktoai.question_service.service;

import com.asktoai.question_service.dto.Answer;
import org.springframework.stereotype.Service;

@Service
public class ChatService {

    public Answer getAnswerFromGemini(String question) {

        //CommonUtils.sleep(Duration.ofSeconds(3));

        return new Answer(1, question, "Here is your answer to your question");
    }
}
