package org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.repositories;

import org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.entities.Appointment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    // CAMBIO: Sustituido 'a.location' por 'a.property.name'
    @Query("SELECT a FROM Appointment a WHERE " +
            "LOWER(a.property.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(a.agent.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(a.client.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Appointment> searchAppointments(@Param("keyword") String keyword, Pageable pageable);

    boolean existsByAgentId(Long agentId);

    boolean existsByClientId(Long clientId);

    boolean existsByPropertyId(Long propertyId);
}