package br.com.urbana.dto.card;

import br.com.urbana.enums.CardRequestStatus;
import br.com.urbana.enums.CardType;

import java.time.LocalDateTime;

public record CardRequestResponseDTO(
        Long id,
        Long userId,
        String userName,
        CardType cardType,
        CardRequestStatus status,
        LocalDateTime createdAt,
        LocalDateTime decidedAt
) {
}
