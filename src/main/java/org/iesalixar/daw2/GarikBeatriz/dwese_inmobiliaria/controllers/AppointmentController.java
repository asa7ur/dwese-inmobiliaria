package org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.controllers;

import org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.entities.Appointment;
import org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.entities.dto.AppointmentDTO;
import org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.repositories.AgentRepository;
import org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.repositories.AppointmentRepository;
import org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.repositories.ClientRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/appointments")
public class AppointmentController {
    private static final Logger logger = LoggerFactory.getLogger(AppointmentController.class);

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private AgentRepository agentRepository;

    @Autowired
    private ClientRepository clientRepository;

    @GetMapping
    public String listAppointments(@RequestParam(defaultValue = "1") int page,
                                   @RequestParam(defaultValue = "") String keyword,
                                   @RequestParam(defaultValue = "id") String sortBy,
                                   @RequestParam(defaultValue = "asc") String direction,
                                   Model model) {
        logger.info("Listing appointments. Page: {}, Keyword: {}, Sort: {}, Dir: {}", page, keyword, sortBy, direction);

        int pageSize = 5;

        Sort sort = direction.equalsIgnoreCase(Sort.Direction.ASC.name()) ?
                Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page - 1, pageSize, sort);

        Page<Appointment> appointmentPage;
        if (keyword == null || keyword.isEmpty()) {
            appointmentPage = appointmentRepository.findAll(pageable);
        } else {
            appointmentPage = appointmentRepository.searchAppointments(keyword, pageable);
        }

        AppointmentDTO appointmentDTO = new AppointmentDTO(
                appointmentPage.getContent(),
                appointmentPage.getTotalPages(),
                page
        );

        logger.info("Se han cargado {} citas.", appointmentDTO.getAppointments().size());
        model.addAttribute("listAppointments", appointmentDTO);
        model.addAttribute("keyword", keyword);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("direction", direction);
        model.addAttribute("reverseSortDir", direction.equals("asc") ? "desc" : "asc");
        model.addAttribute("activePage", "appointments");
        return "appointment";
    }

    @GetMapping("/new")
    public String showNewForm(Model model) {
        logger.info("Mostrando formulario para nueva cita.");
        model.addAttribute("appointment", new Appointment());
        model.addAttribute("agents", agentRepository.findAll());
        model.addAttribute("clients", clientRepository.findAll());
        return "appointment-form";
    }

    @GetMapping("/edit")
    public String showEditForm(@RequestParam("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        logger.info("Mostrando formulario de edición para la cita con ID {}", id);
        Optional<Appointment> appointmentOpt = appointmentRepository.findById(id);

        if (appointmentOpt.isEmpty()) {
            logger.warn("No se encontró la cita con ID {}", id);
            redirectAttributes.addFlashAttribute("message", "Cita no encontrada");
            return "redirect:/appointments";
        }

        model.addAttribute("appointment", appointmentOpt.get());
        model.addAttribute("agents", agentRepository.findAll());
        model.addAttribute("clients", clientRepository.findAll());
        return "appointment-form";
    }

    @PostMapping("/insert")
    public String insertAppointment(@ModelAttribute("appointment") Appointment appointment,
                                    BindingResult result,
                                    Model model,
                                    RedirectAttributes redirectAttributes) {
        logger.info("Insertando nueva cita con código {}", appointment.getCode());

        if(result.hasErrors()){
            logger.warn("Errores de validación en el formulario de nueva cita.");
            model.addAttribute("agents", agentRepository.findAll());
            model.addAttribute("clients", clientRepository.findAll());
            return "appointment-form";
        }

        appointmentRepository.save(appointment);
        logger.info("Cita {} insertada con éxito.", appointment.getCode());
        redirectAttributes.addFlashAttribute("successMessage", "Cita insertada correctamente.");
        return "redirect:/appointments";
    }

    @PostMapping("/update")
    public String updateAppointment(@ModelAttribute("appointment") Appointment appointment,
                                    BindingResult result,
                                    Model model,
                                    RedirectAttributes redirectAttributes) {
        logger.info("Actualizando cita con ID {}", appointment.getId());

        if(result.hasErrors()){
            logger.warn("Errores de validación al actualizar la cita.");
            model.addAttribute("agents", agentRepository.findAll());
            model.addAttribute("clients", clientRepository.findAll());
            return "transaction-form";
        }

        appointmentRepository.save(appointment);
        logger.info("Cita con ID {} actualizada con éxito.", appointment.getId());
        redirectAttributes.addFlashAttribute("successMessage", "Cita actualizada correctamente.");
        return "redirect:/appointments";
    }

    @PostMapping("/delete")
    public String deleteAppointment(@RequestParam("id") Long id, RedirectAttributes redirectAttributes) {
        logger.info("Eliminando cita con ID {}", id);
        appointmentRepository.deleteById(id);
        logger.info("Cita con ID {} eliminada con éxito.", id);
        redirectAttributes.addFlashAttribute("successMessage", "Cita eliminada correctamente.");
        return "redirect:/appointments";
    }
}
