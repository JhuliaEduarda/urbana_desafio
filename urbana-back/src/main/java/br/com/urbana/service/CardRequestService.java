package br.com.urbana.service;

import br.com.urbana.dto.card.CardRequestDTO;
import br.com.urbana.dto.card.CardRequestResponseDTO;
import br.com.urbana.entity.CardRequest;
import br.com.urbana.entity.User;
import br.com.urbana.enums.CardRequestStatus;
import br.com.urbana.repository.CardRequestRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CardRequestService {

    private final CardRequestRepository requestRepository;
    private final UserService userService;
    private final CardService cardService;

    public CardRequestService(
            CardRequestRepository requestRepository,
            UserService userService,
            CardService cardService
    ) {
        this.requestRepository = requestRepository;
        this.userService = userService;
        this.cardService = cardService;
    }

    @Transactional
    public CardRequestResponseDTO create(
            Long userId,
            CardRequestDTO request,
            Long authenticatedUserId,
            boolean admin
    ) {
        if (!userId.equals(authenticatedUserId)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Você só pode solicitar cartões para si mesmo."
            );
        }

        User user = userService.findEntityById(userId);

        CardRequest cardRequest = new CardRequest();
        cardRequest.setUser(user);
        cardRequest.setCardType(request.cardType());
        cardRequest.setStatus(CardRequestStatus.PENDING);

        return toResponseDTO(requestRepository.save(cardRequest));
    }

    @Transactional(readOnly = true)
    public List<CardRequestResponseDTO> findAll(boolean admin) {
        requireAdmin(admin);

        return requestRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<CardRequestResponseDTO> findMine(Long userId) {
        return requestRepository
                .findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    @Transactional
    public CardRequestResponseDTO decide(
            Long requestId,
            boolean approved,
            boolean admin
    ) {
        requireAdmin(admin);

        CardRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Solicitação não encontrada."
                ));

        if (request.getStatus() != CardRequestStatus.PENDING) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Esta solicitação já foi decidida."
            );
        }

        if (approved) {
            cardService.issueActiveCard(
                    request.getUser(),
                    request.getCardType()
            );

            request.setStatus(CardRequestStatus.APPROVED);
        } else {
            request.setStatus(CardRequestStatus.REJECTED);
        }

        request.setDecidedAt(LocalDateTime.now());

        return toResponseDTO(requestRepository.save(request));
    }

    private void requireAdmin(boolean admin) {
        if (!admin) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Apenas administradores podem gerenciar solicitações."
            );
        }
    }

    private CardRequestResponseDTO toResponseDTO(CardRequest request) {
        return new CardRequestResponseDTO(
                request.getId(),
                request.getUser().getId(),
                request.getUser().getName(),
                request.getCardType(),
                request.getStatus(),
                request.getCreatedAt(),
                request.getDecidedAt()
        );
    }
}
