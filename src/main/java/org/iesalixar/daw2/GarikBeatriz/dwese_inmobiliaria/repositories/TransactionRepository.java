package org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.repositories;

import org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.entities.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    @Query("SELECT t FROM Transaction t WHERE " +
            "LOWER(t.property.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(t.client.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(t.agent.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(t.status) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Transaction> searchTransactions(@Param("keyword") String keyword, Pageable pageable);

    boolean existsByPropertyId(Long propertyId);
    boolean existsByAgentId(Long agentId);
    boolean existsByClientId(Long clientId);
}
