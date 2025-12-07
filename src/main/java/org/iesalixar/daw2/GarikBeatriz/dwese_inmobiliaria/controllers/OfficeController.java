package org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.controllers;

import jakarta.validation.Valid;
import org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.entities.Office;
import org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.entities.dto.OfficeDTO;
import org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.repositories.AgentRepository;
import org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.repositories.OfficeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
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
@RequestMapping("/offices")
public class OfficeController {
    private static final Logger logger = LoggerFactory.getLogger(OfficeController.class);

    @Autowired
    private OfficeRepository officeRepository;

    @Autowired
    private AgentRepository agentRepository;

    @Autowired
    private MessageSource messageSource;

    @GetMapping
    public String listOffices(@RequestParam(defaultValue = "1") int page,
                              @RequestParam(defaultValue = "") String keyword,
                              @RequestParam(defaultValue = "id") String sortBy,
                              @RequestParam(defaultValue = "asc") String direction,
                              Model model) {
        logger.info("Listando oficinas. Page: {}, Keyword: {}, Sort: {}, Dir: {}", page, keyword, sortBy, direction);

        int pageSize = 6;

        Sort sort = direction.equalsIgnoreCase(Sort.Direction.ASC.name()) ?
                Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page - 1, pageSize, sort);

        Page<Office> officePage;
        if (keyword == null || keyword.isEmpty()) {
            officePage = officeRepository.findAll(pageable);
        } else {
            officePage = officeRepository.searchOffices(keyword, pageable);
        }

        OfficeDTO officeDTO = new OfficeDTO(
                officePage.getContent(),
                officePage.getTotalPages(),
                page
        );

        logger.info("Se han cargado {} sucursales.", officeDTO.getOffices().size());
        model.addAttribute("listOffices", officeDTO);
        model.addAttribute("keyword", keyword);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("direction", direction);
        model.addAttribute("reverseSortDir", direction.equals("asc") ? "desc" : "asc");
        model.addAttribute("activePage", "offices");
        return "office";
    }

    @GetMapping("/new")
    public String showNewForm(Model model) {
        logger.info("Mostrando formulario para nueva sucursal.");
        model.addAttribute("office", new Office());
        return "office-form";
    }

    @GetMapping("/edit")
    public String showEditForm(@RequestParam("id") Long id,
                               RedirectAttributes redirectAttributes,
                               Model model) {
        logger.info("Mostrando formulario de edición para la sucursal con ID {}", id);
        Optional<Office> officeOpt = officeRepository.findById(id);

        if (officeOpt.isEmpty()) {
            logger.warn("No se encontró la sucursal con ID {}", id);

            String message = messageSource.getMessage("msg.office.flash.not-found", null, LocaleContextHolder.getLocale());
            redirectAttributes.addFlashAttribute("errorMessage", message);
            return "redirect:/offices";
        }

        model.addAttribute("office", officeOpt.get());
        return "office-form";
    }

    @PostMapping("/insert")
    public String insertOffice(@Valid @ModelAttribute("office") Office office,
                               BindingResult result,
                               Model model,
                               RedirectAttributes redirectAttributes) {

        logger.info("Intentando insertar nueva sucursal...");

        if (result.hasErrors()) {
            logger.warn("Errores de validación en el formulario de oficina.");
            return "office-form";
        }

        officeRepository.save(office);
        logger.info("Sucursal {} insertada con éxito.", office.getCode());

        String message = messageSource.getMessage("msg.office.flash.created", null, LocaleContextHolder.getLocale());
        redirectAttributes.addFlashAttribute("successMessage", message);

        return "redirect:/offices";
    }

    @PostMapping("/update")
    public String updateOffice(@Valid @ModelAttribute("office") Office office,
                               BindingResult result,
                               Model model,
                               RedirectAttributes redirectAttributes) {

        logger.info("Actualizando sucursal con ID {}", office.getId());

        if (result.hasErrors()) {
            logger.warn("Errores de validación al actualizar la oficina.");
            return "office-form";
        }

        officeRepository.save(office);
        logger.info("Sucursal con ID {} actualizada con éxito.", office.getId());

        String message = messageSource.getMessage("msg.office.flash.updated", null, LocaleContextHolder.getLocale());
        redirectAttributes.addFlashAttribute("successMessage", message);

        return "redirect:/offices";
    }

    @PostMapping("/delete")
    public String deleteOffice(@RequestParam("id") Long id, RedirectAttributes redirectAttributes) {
        logger.info("Intentando eliminar sucursal con ID {}", id);

        if (agentRepository.existsByOfficeId(id)) {
            logger.warn("Intento de eliminar oficina ID {} fallido: Tiene agentes asignados.", id);

            String message = messageSource.getMessage("msg.office.flash.has-agents", null, LocaleContextHolder.getLocale());
            redirectAttributes.addFlashAttribute("errorMessage", message);
            return "redirect:/offices";
        }

        officeRepository.deleteById(id);
        logger.info("Sucursal con ID {} eliminada con éxito.", id);

        String message = messageSource.getMessage("msg.office.flash.deleted", null, LocaleContextHolder.getLocale());
        redirectAttributes.addFlashAttribute("successMessage", message);

        return "redirect:/offices";
    }

    // Redirecciones de seguridad (get methods for post actions)
    @GetMapping("/update")
    public String redirectLostUpdate(@RequestParam(required = false) Long id) {
        if (id != null) return "redirect:/offices/edit?id=" + id;
        return "redirect:/offices";
    }

    @GetMapping("/insert")
    public String redirectLostInsert() {
        return "redirect:/offices/new";
    }

    @GetMapping({"/delete"})
    public String redirectLostDelete() {
        return "redirect:/offices";
    }
}