package kg.spring.ort.dto.response;

public record QuestionSuggestionAnswerResponse(
        Long id,
        String answerText,
        boolean isCorrect
) {
}

