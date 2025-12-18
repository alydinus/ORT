package kg.spring.ort.dto.request;

import java.util.List;

public record UpdateQuestionRequest(
        String questionText,
        List<UpdateAnswerRequest> answers
) {
}
