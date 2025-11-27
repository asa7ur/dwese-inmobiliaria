package org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.repositories;

import org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.entities.Client;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientRepository extends JpaRepository<Client, Long> {
    @Query("SELECT COUNT(c) > 0 FROM Client c WHERE c.dni =? :dni")
    boolean existsClientByDni(String dni);
}
