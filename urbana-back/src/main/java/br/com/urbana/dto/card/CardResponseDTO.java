package br.com.urbana.dto.card;

import br.com.urbana.enums.CardType;

public record CardResponseDTO(
        Long id,
        String cardNumber,
        String cardName,
        boolean active,
        CardType cardType,
        Long userId
) {
}