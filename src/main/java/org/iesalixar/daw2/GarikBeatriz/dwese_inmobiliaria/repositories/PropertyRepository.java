package org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.repositories;

import org.springframework.data.jdbc.repository.query.Query;

public interface PropertyRepository {

    @Query("SELECT COUNT(p) > 0 FROM properties p WHERE p.code =? :code")
    boolean existsPropertyByCode(String code);
}
