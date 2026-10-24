package br.com.urbana.dto.card;

import br.com.urbana.enums.CardType;
import jakarta.validation.constraints.NotNull;

public record CardRequestDTO(

        @NotNull(message = "Card type is required")
        CardType cardType
) {
}