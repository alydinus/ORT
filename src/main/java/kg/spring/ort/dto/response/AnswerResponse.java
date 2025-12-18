package kg.spring.ort.dto.response;

public record AnswerResponse(
        Long id,
        Long questionId,
        String answerText,
        boolean isCorrect
) {
}
