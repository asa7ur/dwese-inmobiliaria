package org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.controllers;

import jakarta.validation.Valid;
import org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.entities.Office;
import org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.entities.dto.OfficeDTO;
import org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.services.OfficeService;
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
@RequestMapping("/offices")
public class OfficeController {
    private static final Logger logger = LoggerFactory.getLogger(OfficeController.class);

    @Autowired
    private OfficeService officeService;

    @Autowired
    private MessageSource messageSource;

    @GetMapping
    public String listOffices(@RequestParam(defaultValue = "1") int page,
                              @RequestParam(defaultValue = "") String keyword,
                              @RequestParam(defaultValue = "id") String sortBy,
                              @RequestParam(defaultValue = "asc") String direction,
                              Model model) {
        logger.info("Listando oficinas via Service. Page: {}", page);

        OfficeDTO officeDTO = officeService.listOffices(page, 6, sortBy, direction, keyword);

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
        model.addAttribute("office", new Office());
        return "office-form";
    }

    @GetMapping("/edit")
    public String showEditForm(@RequestParam("id") Long id,
                               RedirectAttributes redirectAttributes,
                               Model model) {
        Optional<Office> officeOpt = officeService.findById(id);

        if (officeOpt.isEmpty()) {
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
        if (result.hasErrors()) {
            return "office-form";
        }

        officeService.saveOffice(office);

        String message = messageSource.getMessage("msg.office.flash.created", null, LocaleContextHolder.getLocale());
        redirectAttributes.addFlashAttribute("successMessage", message);

        return "redirect:/offices";
    }

    @PostMapping("/update")
    public String updateOffice(@Valid @ModelAttribute("office") Office office,
                               BindingResult result,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "office-form";
        }

        Office updated = officeService.updateOffice(office);

        if (updated == null) {
            String message = messageSource.getMessage("msg.office.flash.not-found", null, LocaleContextHolder.getLocale());
            redirectAttributes.addFlashAttribute("errorMessage", message);
            return "redirect:/offices";
        }

        String message = messageSource.getMessage("msg.office.flash.updated", null, LocaleContextHolder.getLocale());
        redirectAttributes.addFlashAttribute("successMessage", message);

        return "redirect:/offices";
    }

    @PostMapping("/delete")
    public String deleteOffice(@RequestParam("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            officeService.deleteOffice(id);
            String message = messageSource.getMessage("msg.office.flash.deleted", null, LocaleContextHolder.getLocale());
            redirectAttributes.addFlashAttribute("successMessage", message);
        } catch (Exception e) {
            // Capturamos la excepción de negocio (oficina con agentes)
            String message = messageSource.getMessage(e.getMessage(), null, LocaleContextHolder.getLocale());
            redirectAttributes.addFlashAttribute("errorMessage", message);
        }
        return "redirect:/offices";
    }

    // Redirecciones de seguridad
    @GetMapping("/update")
    public String redirectLostUpdate(@RequestParam(required = false) Long id) {
        return (id != null) ? "redirect:/offices/edit?id=" + id : "redirect:/offices";
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