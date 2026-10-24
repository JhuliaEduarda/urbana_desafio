package br.com.urbana.controller;

import br.com.urbana.dto.card.CardRequestDTO;
import br.com.urbana.dto.card.CardResponseDTO;
import br.com.urbana.service.CardService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cards")
public class CardController {

    private final CardService cardService;

    public CardController(CardService cardService) {
        this.cardService = cardService;
    }

    @PostMapping("/user/{userId}")
    public ResponseEntity<CardResponseDTO> create(
            @PathVariable Long userId,
            @Valid @RequestBody CardRequestDTO request,
            Authentication authentication
    ) {
        CardResponseDTO card = cardService.create(
                userId,
                request,
                isAdmin(authentication)
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(card);
    }

    @GetMapping
    public ResponseEntity<List<CardResponseDTO>> findAll(
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                cardService.findAll(
                        authenticatedUserId(authentication),
                        isAdmin(authentication)
                )
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<CardResponseDTO> findById(
            @PathVariable Long id,
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                cardService.findById(
                        id,
                        authenticatedUserId(authentication),
                        isAdmin(authentication)
                )
        );
    }


    @PatchMapping("/{id}/status")
    public ResponseEntity<CardResponseDTO> updateStatus(
            @PathVariable Long id,
            @RequestParam boolean active,
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                cardService.updateStatus(
                        id,
                        active,
                        isAdmin(authentication)
                )
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            Authentication authentication
    ) {
        cardService.delete(id, isAdmin(authentication));
        return ResponseEntity.noContent().build();
    }

    private Long authenticatedUserId(Authentication authentication) {
        return Long.parseLong(authentication.getName());
    }

    private boolean isAdmin(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .anyMatch(authority ->
                        "ROLE_ADMIN".equals(authority.getAuthority()));
    }
}
