package org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.repositories;

import org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.entities.Agent;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AgentRepository extends JpaRepository<Agent, Long> {
    @Query("SELECT COUNT(a) > 0 FROM Agent a WHERE a.dni =? :dni")
    boolean existsAgentByDni(String dni);

    @Query("SELECT COUNT(a) > 0 FROM Agent a WHERE a.dni = :dni AND a.id <> :id")
    boolean existsAgentByDniAndIdNot(String dni, Long id);

}
