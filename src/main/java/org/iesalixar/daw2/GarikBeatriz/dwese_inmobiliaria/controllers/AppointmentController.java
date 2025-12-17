package org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.controllers;

import jakarta.validation.Valid;
import org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.components.AgentPropertyValidator;
import org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.entities.Appointment;
import org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.entities.dto.AppointmentDTO;
import org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.repositories.AgentRepository;
import org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.repositories.ClientRepository;
import org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.repositories.PropertyRepository;
import org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.services.AppointmentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
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
    private AppointmentService appointmentService;

    @Autowired
    private PropertyRepository propertyRepository;

    @Autowired
    private AgentRepository agentRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private AgentPropertyValidator agentPropertyValidator;

    @GetMapping
    public String listAppointments(@RequestParam(defaultValue = "1") int page,
                                   @RequestParam(defaultValue = "") String keyword,
                                   @RequestParam(defaultValue = "appointmentTimestamp") String sortBy,
                                   @RequestParam(defaultValue = "desc") String direction,
                                   Model model) {
        logger.info("Listing appointments via Service. Page: {}", page);

        AppointmentDTO appointmentDTO = appointmentService.listAppointments(page, 5, sortBy, direction, keyword);

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
        model.addAttribute("appointment", new Appointment());
        loadFormDependencies(model);
        return "appointment-form";
    }

    @GetMapping("/edit")
    public String showEditForm(@RequestParam("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Appointment> appointmentOpt = appointmentService.findById(id);

        if (appointmentOpt.isEmpty()) {
            String message = messageSource.getMessage("msg.appointment.flash.not-found", null, LocaleContextHolder.getLocale());
            redirectAttributes.addFlashAttribute("errorMessage", message);
            return "redirect:/appointments";
        }

        model.addAttribute("appointment", appointmentOpt.get());
        loadFormDependencies(model);
        return "appointment-form";
    }

    @PostMapping("/insert")
    public String insertAppointment(@Valid @ModelAttribute("appointment") Appointment appointment,
                                    BindingResult result,
                                    Model model,
                                    RedirectAttributes redirectAttributes) {

        validateAgentProperty(appointment, result);

        if(result.hasErrors()){
            loadFormDependencies(model);
            return "appointment-form";
        }

        appointmentService.saveAppointment(appointment);

        String message = messageSource.getMessage("msg.appointment.flash.created", null, LocaleContextHolder.getLocale());
        redirectAttributes.addFlashAttribute("successMessage", message);
        return "redirect:/appointments";
    }

    @PostMapping("/update")
    public String updateAppointment(@Valid @ModelAttribute("appointment") Appointment appointment,
                                    BindingResult result,
                                    Model model,
                                    RedirectAttributes redirectAttributes) {

        validateAgentProperty(appointment, result);

        if(result.hasErrors()){
            loadFormDependencies(model);
            return "appointment-form";
        }

        Appointment updated = appointmentService.updateAppointment(appointment);

        if (updated == null) {
            String message = messageSource.getMessage("msg.appointment.flash.not-found", null, LocaleContextHolder.getLocale());
            redirectAttributes.addFlashAttribute("errorMessage", message);
            return "redirect:/appointments";
        }

        String message = messageSource.getMessage("msg.appointment.flash.updated", null, LocaleContextHolder.getLocale());
        redirectAttributes.addFlashAttribute("successMessage", message);
        return "redirect:/appointments";
    }

    @PostMapping("/delete")
    public String deleteAppointment(@RequestParam("id") Long id, RedirectAttributes redirectAttributes) {
        appointmentService.deleteAppointment(id);

        String message = messageSource.getMessage("msg.appointment.flash.deleted", null, LocaleContextHolder.getLocale());
        redirectAttributes.addFlashAttribute("successMessage", message);
        return "redirect:/appointments";
    }

    private void loadFormDependencies(Model model) {
        model.addAttribute("agents", agentRepository.findAll());
        model.addAttribute("clients", clientRepository.findAll());
        model.addAttribute("properties", propertyRepository.findAll());
    }

    private void validateAgentProperty(Appointment appointment, BindingResult result) {
        if (!result.hasFieldErrors("property") && !result.hasFieldErrors("agent")) {
            agentPropertyValidator.validate(
                    appointment.getProperty().getId(),
                    appointment.getAgent().getId(),
                    result,
                    "agent"
            );
        }
    }

    @GetMapping("/update")
    public String redirectLostUpdate(@RequestParam(required = false) Long id) {
        return (id != null) ? "redirect:/appointments/edit?id=" + id : "redirect:/appointments";
    }

    @GetMapping("/insert")
    public String redirectLostInsert() {
        return "redirect:/appointments/new";
    }

    @GetMapping({"/delete"})
    public String redirectLostDelete() {
        return "redirect:/appointments";
    }
}