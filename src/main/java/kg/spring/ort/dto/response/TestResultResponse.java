package kg.spring.ort.dto.response;

import java.time.LocalDateTime;

public record TestResultResponse(Long id, Long testId, String testTitle, Integer score, LocalDateTime date) {
}

