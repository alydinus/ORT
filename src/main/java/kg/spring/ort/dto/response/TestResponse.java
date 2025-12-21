package kg.spring.ort.dto.response;

import java.util.List;

public record TestResponse(
        Long id,
        String title,
        String description,
        Integer durationMinutes,
        boolean isActive,
        String theme,
        String authorUsername,
        List<String> tags,
        List<QuestionResponse> questions
) {
}
