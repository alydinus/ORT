package kg.spring.ort.dto.response;

public record RegisterResponse(
        Long id,
        String email,
        String username
) {
}
