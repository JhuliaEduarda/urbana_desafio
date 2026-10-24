package br.com.urbana.service;

import br.com.urbana.dto.card.CardRequestDTO;
import br.com.urbana.dto.card.CardResponseDTO;
import br.com.urbana.entity.Card;
import br.com.urbana.entity.User;
import br.com.urbana.enums.CardType;
import br.com.urbana.exception.CardNotFoundException;
import br.com.urbana.repository.CardRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class CardService {

    private final CardRepository cardRepository;
    private final UserService userService;

    public CardService(
            CardRepository cardRepository,
            UserService userService
    ) {
        this.cardRepository = cardRepository;
        this.userService = userService;
    }

    public CardResponseDTO create(
            Long userId,
            CardRequestDTO request,
            boolean admin
    ) {
        requireAdmin(admin);

        User user = userService.findEntityById(userId);

        Card card = issueActiveCard(user, request.cardType());

        return toResponseDTO(card);
    }

    public Card issueActiveCard(User user, CardType cardType) {

        Card card = new Card();

        card.setCardNumber(generateUniqueCardNumber());
        card.setCardName(user.getName());
        card.setCardType(cardType);
        card.setActive(true);
        card.setUser(user);

        return cardRepository.save(card);
    }

    public List<CardResponseDTO> findAll(
            Long authenticatedUserId,
            boolean admin
    ) {
        List<Card> cards = admin
                ? cardRepository.findAll()
                : cardRepository.findByUserId(authenticatedUserId);

        return cards.stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public CardResponseDTO findById(
            Long id,
            Long authenticatedUserId,
            boolean admin
    ) {
        Card card = findEntityById(id);
        checkOwnership(card, authenticatedUserId, admin);

        return toResponseDTO(card);
    }

    public CardResponseDTO updateStatus(Long id, boolean active, boolean admin) {
        requireAdmin(admin);

        Card card = findEntityById(id);
        card.setActive(active);

        return toResponseDTO(cardRepository.save(card));
    }

    public void delete(Long id, boolean admin) {
        requireAdmin(admin);
        cardRepository.delete(findEntityById(id));
    }

    public Card findEntityById(Long id) {
        return cardRepository.findById(id)
                .orElseThrow(CardNotFoundException::new);
    }

    private void checkOwnership(
            Card card,
            Long authenticatedUserId,
            boolean admin
    ) {
        boolean isOwner = card.getUser().getId().equals(authenticatedUserId);

        if (!admin && !isOwner) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Você não tem permissão para acessar este cartão."
            );
        }
    }

    private void requireAdmin(boolean admin) {
        if (!admin) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Apenas administradores podem realizar esta operação."
            );
        }
    }

    private String generateUniqueCardNumber() {
        String cardNumber;

        do {
            int number = ThreadLocalRandom.current()
                    .nextInt(10_000_000, 100_000_000);

            int digit = ThreadLocalRandom.current().nextInt(10);

            cardNumber = String.format("90.03.%08d-%d", number, digit);
        } while (cardRepository.existsByCardNumber(cardNumber));

        return cardNumber;
    }

    private CardResponseDTO toResponseDTO(Card card) {
        return new CardResponseDTO(
                card.getId(),
                card.getCardNumber(),
                card.getCardName(),
                card.isActive(),
                card.getCardType(),
                card.getUser().getId()
        );
    }
}
