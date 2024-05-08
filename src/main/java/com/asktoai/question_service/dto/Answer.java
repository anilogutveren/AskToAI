package com.asktoai.question_service.dto;

public record Answer(
        Integer id,
        String question ,
        String answerText) {
}
