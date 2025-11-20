package org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.repositories;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TransactionRepository {
    boolean existsTransactionByCode(String code);

    @Query("SELECT COUNT(a) > 0 FROM Transaction a WHERE a.code = :code AND a.id != :id")
    boolean existsTransactionByCodeAndNotId(@Param("code") String code, @Param("id") Long id);
}
