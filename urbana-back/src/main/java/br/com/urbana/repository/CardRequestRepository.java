package br.com.urbana.repository;

import br.com.urbana.entity.CardRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Modifying;

import java.util.List;

public interface CardRequestRepository
        extends JpaRepository<CardRequest, Long> {

    @Query(
        value = "SELECT * FROM card_requests ORDER BY created_at DESC",
        nativeQuery = true
    )
    List<CardRequest> findAllByOrderByCreatedAtDesc();

    @Query(
        value = """
            SELECT *
            FROM card_requests
            WHERE user_id = :userId
            ORDER BY created_at DESC
            """,
        nativeQuery = true
    )
    List<CardRequest> findByUserIdOrderByCreatedAtDesc(
            @Param("userId") Long userId
    );

    @Modifying
    @Query(
        value = "DELETE FROM card_requests WHERE user_id = :userId",
        nativeQuery = true
    )
    void deleteByUserId(@Param("userId") Long userId);
}
