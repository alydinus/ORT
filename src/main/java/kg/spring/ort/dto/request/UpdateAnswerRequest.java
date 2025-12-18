package kg.spring.ort.dto.request;

public record UpdateAnswerRequest(
        Long answerId,
        String answerText,
        boolean isCorrect
) {
}
