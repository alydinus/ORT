package kg.spring.ort.dto.request;

public record CreateAnswerRequest(
        String answerText,
        boolean isCorrect
) {
}
