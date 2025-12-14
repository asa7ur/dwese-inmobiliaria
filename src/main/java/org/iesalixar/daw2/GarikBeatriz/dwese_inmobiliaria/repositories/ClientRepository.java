package org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.repositories;

import org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.entities.Client;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ClientRepository extends JpaRepository<Client, Long> {
    @Query("SELECT COUNT(c) > 0 FROM Client c WHERE LOWER(c.dni) = LOWER(:dni)")
    boolean existsClientByDni(@Param("dni") String dni);

    @Query("SELECT COUNT(c) > 0 FROM Client c WHERE LOWER(c.dni) = LOWER(:dni) AND c.id <> :id")
    boolean existsClientByDniAndIdNot(@Param("dni") String dni, @Param("id") Long id);

    @Query("SELECT c FROM Client c WHERE " +
            "LOWER(c.dni) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(c.email) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Client> searchClients(@Param("keyword") String keyword, Pageable pageable);
}
