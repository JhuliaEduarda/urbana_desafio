package br.com.urbana.dto.auth;

public record LoginResponseDTO(
    Long id,
    String name,
    String email,
    String token
) {}