package org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.controllers;

import org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.entities.Appointment;
import org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.repositories.AppointmentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/appointment")
public class AppointmentController {
    private static final Logger logger = LoggerFactory.getLogger(AppointmentController.class);

    @Autowired
    private AppointmentRepository appointmentRepository;

    @GetMapping
    public String listAppointments(Model model) {
        logger.info("Solicitando la lista de todas las citas...");
        List<Appointment> listAppointments = appointmentRepository.findAll();
        logger.info("Se han cargado {} citas.", listAppointments.size());
        model.addAttribute("listAppointments", listAppointments);
        return "appointment";
    }

    @GetMapping("/new")
    public String showNewForm(Model model) {
        logger.info("Mostrando formulario para nueva cita.");
        model.addAttribute("appointment", new Appointment());
        return "appointment-form";
    }

    @GetMapping("/edit")
    public String showEditForm(@RequestParam("id") Long id, Model model) {
        logger.info("Mostrando formulario de edición para la cita con ID {}", id);
        Optional<Appointment> appointmentOpt = appointmentRepository.findById(id);
        if (appointmentOpt.isEmpty()) {
            logger.warn("No se encontró la cita con ID {}", id);
        }
        model.addAttribute("appointment", appointmentOpt);
        return "appointment-form";
    }

    @PostMapping("/insert")
    public String insertAppointment(@ModelAttribute("appointment") Appointment appointment) {
        logger.info("Insertando nueva cita con código {}", appointment.getCode());
        appointmentRepository.save(appointment);
        logger.info("Cita {} insertada con éxito.", appointment.getCode());
        return "redirect:/appointments";
    }

    @PostMapping("/update")
    public String updateAppointment(@ModelAttribute("appointment") Appointment appointment) {
        logger.info("Actualizando cita con ID {}", appointment.getId());
        appointmentRepository.save(appointment);
        logger.info("Cita con ID {} actualizada con éxito.", appointment.getId());
        return "redirect:/appointments";
    }

    @PostMapping("/delete")
    public String deleteAppointment(@RequestParam("id") Long id) {
        logger.info("Eliminando cita con ID {}", id);
        appointmentRepository.deleteById(id);
        logger.info("Cita con ID {} eliminada con éxito.", id);
        return "redirect:/appointments";
    }
}
