package org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.repositories;

import org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.entities.Property;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PropertyRepository extends JpaRepository<Property, Long> {

    @Query("SELECT p FROM Property p WHERE " +
            "LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(p.location) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(CAST(p.type AS string)) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(CAST(p.status AS string)) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Property> searchProperties(@Param("keyword") String keyword, Pageable pageable);
}