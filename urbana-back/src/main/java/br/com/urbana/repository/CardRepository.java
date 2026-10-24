package br.com.urbana.repository;

import br.com.urbana.entity.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CardRepository extends JpaRepository<Card, Long> {

    boolean existsByCardNumber(String cardNumber);

    @Query(
        value = "SELECT * FROM cards WHERE user_id = :userId",
        nativeQuery = true
    )
    List<Card> findByUserId(@Param("userId") Long userId);
}