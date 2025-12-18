package kg.spring.ort.dto.response;

public record TokenPair(
        String accessToken,
        String refreshToken
) {
}
