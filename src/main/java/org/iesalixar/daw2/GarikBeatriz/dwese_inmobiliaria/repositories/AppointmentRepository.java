package org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.repositories;

import org.springframework.data.jdbc.repository.query.Query;

public interface AppointmentRepository {

    @Query("SELECT COUNT(a) > 0 FROM appointments a WHERE a.code =? :code")
    boolean existsAppointmentByCode(String code);
}
