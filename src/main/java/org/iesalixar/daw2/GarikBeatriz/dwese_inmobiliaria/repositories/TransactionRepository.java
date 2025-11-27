package org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.repositories;

import org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.entities.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
}
