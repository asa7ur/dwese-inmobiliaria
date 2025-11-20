package org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.repositories;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ClientRepository {
    boolean existsClientByCode(String code);

    @Query("SELECT COUNT(a) > 0 FROM Client a WHERE a.code = :code AND a.id != :id")
    boolean existsClientByCodeAndNotId(@Param("code") String code, @Param("id") Long id);
}
