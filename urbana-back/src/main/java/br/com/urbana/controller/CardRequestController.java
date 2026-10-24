package br.com.urbana.controller;

import br.com.urbana.dto.card.CardRequestDTO;
import br.com.urbana.dto.card.CardRequestResponseDTO;
import br.com.urbana.service.CardRequestService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/card-requests")
public class CardRequestController {

    private final CardRequestService cardRequestService;

    public CardRequestController(CardRequestService cardRequestService) {
        this.cardRequestService = cardRequestService;
    }

    @PostMapping("/user/{userId}")
    public ResponseEntity<CardRequestResponseDTO> create(
            @PathVariable Long userId,
            @Valid @RequestBody CardRequestDTO request,
            Authentication authentication
    ) {
        CardRequestResponseDTO response = cardRequestService.create(
                userId,
                request,
                authenticatedUserId(authentication),
                isAdmin(authentication)
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<CardRequestResponseDTO>> findAll(
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                cardRequestService.findAll(isAdmin(authentication))
        );
    }

    @GetMapping("/mine")
    public ResponseEntity<List<CardRequestResponseDTO>> findMine(
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                cardRequestService.findMine(
                        authenticatedUserId(authentication)
                )
        );
    }

    @PatchMapping("/{id}/decision")
    public ResponseEntity<CardRequestResponseDTO> decide(
            @PathVariable Long id,
            @RequestParam boolean approved,
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                cardRequestService.decide(
                        id,
                        approved,
                        isAdmin(authentication)
                )
        );
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
