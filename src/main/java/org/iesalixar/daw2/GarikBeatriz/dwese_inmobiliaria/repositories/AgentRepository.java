package org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.repositories;

import org.springframework.data.jdbc.repository.query.Query;

public interface AgentRepository {
    @Query("SELECT COUNT(a) > 0 FROM agents a WHERE a.code =? :code")
    boolean existsAgentByCode(String code);
}
