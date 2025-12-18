package kg.spring.ort.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record RegisterRequest(
        @Email
        String email,
        @NotNull
        String password,
        @NotNull
        String username
) {
}
