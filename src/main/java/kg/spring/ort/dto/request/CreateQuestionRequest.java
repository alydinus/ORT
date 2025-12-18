package kg.spring.ort.dto.request;

import java.util.List;

public record CreateQuestionRequest(
        String questionText,
        List<CreateAnswerRequest> answers
) {
}
