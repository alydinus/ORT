package kg.spring.ort.dto.request;

public record ResetPasswordRequest(String token, String newPassword) {
}
