package org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.repositories;

import org.springframework.data.jdbc.repository.query.Query;

public interface ClientRepository {
    @Query("SELECT COUNT(c) > 0 FROM clients c WHERE c.code =? :code")
    boolean existsClientByCode(String code);
}
