package ge.edu.freeuni.petcarebackend.security.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AuthorizationTokensDTO(
        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        String accessToken,
        String refreshToken
) {
}
