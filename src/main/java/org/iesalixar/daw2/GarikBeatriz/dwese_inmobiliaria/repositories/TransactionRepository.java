package org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.repositories;

import org.springframework.data.jdbc.repository.query.Query;

public interface TransactionRepository {
    @Query("SELECT COUNT(t) > 0 FROM transactions t WHERE t.code =? :code")
    boolean existsTransactionByCode(String code);
}
