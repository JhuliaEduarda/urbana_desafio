package br.com.urbana.service;

import br.com.urbana.dto.card.CardRequestDTO;
import br.com.urbana.dto.card.CardResponseDTO;
import br.com.urbana.entity.Card;
import br.com.urbana.entity.User;
import br.com.urbana.exception.CardNotFoundException;
import br.com.urbana.repository.CardRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class CardService {

    private final CardRepository cardRepository;
    private final UserService userService;

    public CardService(CardRepository cardRepository, UserService userService) {
        this.cardRepository = cardRepository;
        this.userService = userService;
    }

    public CardResponseDTO create(Long userId, CardRequestDTO request) {

        User user = userService.findEntityById(userId);

        Card card = new Card();

        card.setCardNumber(generateUniqueCardNumber());
        card.setCardName(user.getName());
        card.setCardType(request.cardType());
        card.setActive(true);
        card.setUser(user);

        Card savedCard = cardRepository.save(card);

        return toResponseDTO(savedCard);
    }

    public List<CardResponseDTO> findAll() {
        return cardRepository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    private String generateUniqueCardNumber() {

    String cardNumber;

    do {
        int number = ThreadLocalRandom.current()
                .nextInt(10_000_000, 100_000_000);

        int digit = ThreadLocalRandom.current()
                .nextInt(10);

        cardNumber = String.format(
                "90.03.%08d-%d",
                number,
                digit
        );

    } while (cardRepository.existsByCardNumber(cardNumber));

    return cardNumber;
}

    public CardResponseDTO findById(Long id) {
        Card card = findEntityById(id);

        return toResponseDTO(card);
    }

    public Card findEntityById(Long id) {
        return cardRepository.findById(id)
                .orElseThrow(CardNotFoundException::new);
    }

    public CardResponseDTO updateStatus(Long id, boolean active) {

        Card card = findEntityById(id);

        card.setActive(active);

        Card updatedCard = cardRepository.save(card);

        return toResponseDTO(updatedCard);
    }

    public void delete(Long id) {

        Card card = findEntityById(id);

        cardRepository.delete(card);
    }

    private CardResponseDTO toResponseDTO(Card card) {
        return new CardResponseDTO(
                card.getId(),
                card.getCardNumber(),
                card.getCardName(),
                card.isActive(),
                card.getCardType(),
                card.getUser().getId());
    }
}