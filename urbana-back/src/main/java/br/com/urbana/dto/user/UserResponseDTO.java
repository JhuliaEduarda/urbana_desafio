package br.com.urbana.dto.user;

public record UserResponseDTO(
        Long id,
        String name,
        String email,
        String status
) {
}

