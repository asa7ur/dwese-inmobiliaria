package org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.services;

import org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.entities.Appointment;
import org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.entities.dto.AppointmentDTO;
import org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.repositories.AppointmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepository;

    public AppointmentDTO listAppointments(int page, int size, String sortBy, String direction, String keyword) {
        Sort sort = direction.equalsIgnoreCase(Sort.Direction.ASC.name()) ?
                Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page - 1, size, sort);
        Page<Appointment> appointmentPage;

        if (keyword == null || keyword.isEmpty()) {
            appointmentPage = appointmentRepository.findAll(pageable);
        } else {
            appointmentPage = appointmentRepository.searchAppointments(keyword, pageable);
        }

        return new AppointmentDTO(
                appointmentPage.getContent(),
                appointmentPage.getTotalPages(),
                page
        );
    }

    public Optional<Appointment> findById(Long id) {
        return appointmentRepository.findById(id);
    }

    @Transactional
    public void saveAppointment(Appointment appointment) {
        appointmentRepository.save(appointment);
    }

    @Transactional
    public Appointment updateAppointment(Appointment appointment) {
        if (appointment.getId() != null && appointmentRepository.existsById(appointment.getId())) {
            return appointmentRepository.save(appointment);
        }
        return null;
    }

    @Transactional
    public void deleteAppointment(Long id) {
        appointmentRepository.deleteById(id);
    }
}