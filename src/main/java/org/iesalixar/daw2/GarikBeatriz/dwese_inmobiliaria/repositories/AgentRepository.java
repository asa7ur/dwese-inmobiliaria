package org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.repositories;

import org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.entities.Agent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AgentRepository extends JpaRepository<Agent, Long> {
    @Query("SELECT COUNT(a) > 0 FROM Agent a WHERE a.dni = :dni")
    boolean existsAgentByDni(String dni);

    @Query("SELECT COUNT(a) > 0 FROM Agent a WHERE a.dni = :dni AND a.id <> :id")
    boolean existsAgentByDniAndIdNot(String dni, Long id);

    @Query("SELECT a FROM Agent a WHERE " +
            "LOWER(a.dni) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(a.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(a.email) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(a.office.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Agent> searchAgents(@Param("keyword") String keyword, Pageable pageable);

}
