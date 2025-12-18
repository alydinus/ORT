package kg.spring.ort.dto.response;

public record ErrorResponse(
        String errorCode,
        String errorMessage,
        int status,
        long timestamp
) {
}
