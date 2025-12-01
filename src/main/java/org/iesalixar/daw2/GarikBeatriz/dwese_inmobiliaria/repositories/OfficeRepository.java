package org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.repositories;

import org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.entities.Office;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OfficeRepository extends JpaRepository<Office, Long> {
    @Query("SELECT o FROM Office o WHERE " +
            "LOWER(o.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(o.address) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Office> searchOffices(@Param("keyword") String keyword, Pageable pageable);
}
