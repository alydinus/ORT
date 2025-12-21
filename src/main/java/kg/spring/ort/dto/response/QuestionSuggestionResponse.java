package kg.spring.ort.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public record QuestionSuggestionResponse(
        Long id,
        String authorUsername,
        String questionText,
        String questionType,
        Integer points,
        String status,
        LocalDateTime createdAt,
        List<QuestionSuggestionAnswerResponse> answers
) {
}

