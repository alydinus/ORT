package kg.spring.ort.dto.response;

import kg.spring.ort.entity.QuestionType;

import java.util.List;

public record QuestionResponse(Long id, String questionText, QuestionType questionType, Integer points, List<AnswerResponse> answers) {
}

