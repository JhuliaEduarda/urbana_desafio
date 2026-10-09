package br.com.urbana.controller;

import br.com.urbana.dto.card.CardRequestDTO;
import br.com.urbana.dto.card.CardResponseDTO;
import br.com.urbana.service.CardService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
            @Valid @RequestBody CardRequestDTO request) {

        CardResponseDTO card =
                cardService.create(userId, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(card);
    }

    @GetMapping
    public ResponseEntity<List<CardResponseDTO>> findAll() {

        return ResponseEntity.ok(cardService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CardResponseDTO> findById(
            @PathVariable Long id) {

        return ResponseEntity.ok(cardService.findById(id));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<CardResponseDTO> updateStatus(
            @PathVariable Long id,
            @RequestParam boolean active) {

        return ResponseEntity.ok(
                cardService.updateStatus(id, active)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id) {

        cardService.delete(id);

        return ResponseEntity.noContent().build();
    }
}