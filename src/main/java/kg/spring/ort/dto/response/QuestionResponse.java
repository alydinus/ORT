package kg.spring.ort.dto.response;

import java.util.List;

public record QuestionResponse(
        Long id,
        String questionText,
        List<AnswerResponse> answers
) {
}
