package kg.spring.ort.dto.response;

public record LoginResponse(
        String accessToken,
        TokenPair tokens
) {
}
