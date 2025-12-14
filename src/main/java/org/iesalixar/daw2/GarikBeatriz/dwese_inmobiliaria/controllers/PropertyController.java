package org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.controllers;

import jakarta.validation.Valid;
import org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.entities.Property;
import org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.entities.dto.PropertyDTO;
import org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.repositories.AgentRepository;
import org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.services.PropertyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/properties")
public class PropertyController {
    private static final Logger logger = LoggerFactory.getLogger(PropertyController.class);

    @Autowired
    private PropertyService propertyService;

    @Autowired
    private AgentRepository agentRepository;

    @Autowired
    private MessageSource messageSource;

    @GetMapping
    public String listProperties(@RequestParam(defaultValue = "1") int page,
                                 @RequestParam(defaultValue = "") String keyword,
                                 @RequestParam(defaultValue = "id") String sortBy,
                                 @RequestParam(defaultValue = "asc") String direction,
                                 Model model) {

        logger.info("Listing properties via Service. Page: {}", page);

        PropertyDTO propertyDTO = propertyService.listProperties(page, 6, sortBy, direction, keyword);

        model.addAttribute("listProperties", propertyDTO);
        model.addAttribute("keyword", keyword);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("direction", direction);
        model.addAttribute("reverseSortDir", direction.equals("asc") ? "desc" : "asc");
        model.addAttribute("activePage", "properties");
        return "property";
    }

    @GetMapping("/new")
    public String showNewForm(Model model) {
        model.addAttribute("property", new Property());
        loadFormDependencies(model);
        return "property-form";
    }

    @GetMapping("/edit")
    public String showEditForm(
            @RequestParam("id") Long id,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        Optional<Property> propertyOpt = propertyService.findById(id);

        if (propertyOpt.isEmpty()) {
            String message = messageSource.getMessage("msg.property.flash.not-found", null, LocaleContextHolder.getLocale());
            redirectAttributes.addFlashAttribute("errorMessage", message);
            return "redirect:/properties";
        }

        model.addAttribute("property", propertyOpt.get());
        loadFormDependencies(model);
        return "property-form";
    }

    @PostMapping("/insert")
    public String insertProperty(
            @Valid @ModelAttribute("property") Property property,
            BindingResult result,
            @RequestParam("files") MultipartFile[] files,
            Model model,
            RedirectAttributes redirectAttributes) {

        if(result.hasErrors()){
            loadFormDependencies(model);
            return "property-form";
        }

        propertyService.saveProperty(property, files);

        String message = messageSource.getMessage("msg.property.flash.created", null, LocaleContextHolder.getLocale());
        redirectAttributes.addFlashAttribute("successMessage", message);
        return "redirect:/properties";
    }

    @PostMapping("/update")
    public String updateProperty(
            @Valid @ModelAttribute("property") Property property,
            BindingResult result,
            @RequestParam("files") MultipartFile[] files,
            Model model,
            RedirectAttributes redirectAttributes) {

        if(result.hasErrors()){
            loadFormDependencies(model);
            // Recuperar imágenes antiguas para la vista en caso de error
            if (property.getId() != null) {
                Optional<Property> optProperty = propertyService.findById(property.getId());
                optProperty.ifPresent(value -> property.setImages(value.getImages()));
            }
            return "property-form";
        }

        Property updated = propertyService.updateProperty(property, files);

        if (updated != null) {
            String message = messageSource.getMessage("msg.property.flash.updated", null, LocaleContextHolder.getLocale());
            redirectAttributes.addFlashAttribute("successMessage", message);
        } else {
            String message = messageSource.getMessage("msg.property.flash.not-found", null, LocaleContextHolder.getLocale());
            redirectAttributes.addFlashAttribute("errorMessage", message);
        }

        return "redirect:/properties";
    }

    @PostMapping("/delete")
    public String deleteProperty(@RequestParam("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            propertyService.deleteProperty(id);
            String message = messageSource.getMessage("msg.property.flash.deleted", null, LocaleContextHolder.getLocale());
            redirectAttributes.addFlashAttribute("successMessage", message);
        } catch (Exception e) {
            // Captura errores de negocio (tiene transacciones o citas)
            String message = messageSource.getMessage(e.getMessage(), null, LocaleContextHolder.getLocale());
            redirectAttributes.addFlashAttribute("errorMessage", message);
        }
        return "redirect:/properties";
    }

    @PostMapping("/delete-image")
    public String deleteImage(
            @RequestParam("imageId") Long imageId,
            @RequestParam("propertyId") Long propertyId,
            RedirectAttributes redirectAttributes) {

        propertyService.deletePropertyImage(propertyId, imageId);

        String message = messageSource.getMessage("msg.property.flash.image-deleted", null, LocaleContextHolder.getLocale());
        redirectAttributes.addFlashAttribute("successMessage", message);

        return "redirect:/properties/edit?id=" + propertyId;
    }

    // --- Método Auxiliar ---
    private void loadFormDependencies(Model model) {
        // Carga listas para los selects del formulario
        model.addAttribute("types", Property.Type.values());
        model.addAttribute("statuses", Property.Status.values());
        model.addAttribute("agents", agentRepository.findAll());
    }

    // --- Redirecciones de seguridad ---
    @GetMapping("/update")
    public String redirectLostUpdate(@RequestParam(required = false) Long id) {
        return (id != null) ? "redirect:/properties/edit?id=" + id : "redirect:/properties";
    }

    @GetMapping("/insert")
    public String redirectLostInsert() {
        return "redirect:/properties/new";
    }

    @GetMapping({"/delete", "/delete-image"})
    public String redirectLostDelete() {
        return "redirect:/properties";
    }
}