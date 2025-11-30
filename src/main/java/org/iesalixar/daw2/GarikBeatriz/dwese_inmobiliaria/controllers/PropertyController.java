package org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.controllers;

import jakarta.validation.Valid;
import org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.entities.Property;
import org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.entities.Transaction;
import org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.repositories.AgentRepository;
import org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.repositories.PropertyRepository;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/properties")
public class PropertyController {
    private static final Logger logger = LoggerFactory.getLogger(PropertyController.class);

    @Autowired
    private PropertyRepository propertyRepository;

    @Autowired
    private AgentRepository agentRepository;

    @GetMapping
    public String listProperties(Model model) {
        logger.info("Solicitando la lista de todas las propiedades...");
        List<Property> listProperties = propertyRepository.findAll();
        logger.info("Se han cargado {} propiedades.", listProperties.size());
        model.addAttribute("listProperties", listProperties);
        return "property";
    }

    @GetMapping("/new")
    public String showNewForm(Model model) {
        logger.info("Mostrando formulario para nueva propiedad.");
        model.addAttribute("property", new Property());
        model.addAttribute("agents", agentRepository.findAll());
        model.addAttribute("types", Property.Type.values());
        model.addAttribute("statuses", Property.Status.values());
        return "property-form";
    }

    @GetMapping("/edit")
    public String showEditForm(
            @RequestParam("id") Long id,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        logger.info("Mostrando formulario de edición para la propiedad con ID {}", id);
        Optional<Property> propertyOpt = propertyRepository.findById(id);

        if (propertyOpt.isEmpty()) {
            logger.warn("No se encontró la propiedad con ID {}", id);
            redirectAttributes.addFlashAttribute("message", "Propiedad no encontrada");
            return "redirect:/properties";
        }

        model.addAttribute("property", propertyOpt.get());
        model.addAttribute("agents", agentRepository.findAll());
        model.addAttribute("types", Property.Type.values());
        model.addAttribute("statuses", Property.Status.values());
        return "property-form";
    }

    @PostMapping("/insert")
    public String insertProperty(
            @Valid @ModelAttribute("property") Property property,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {
        logger.info("Insertando nueva propiedad con ID {}", property.getId());
        if(result.hasErrors()){
            logger.warn("Errores de validación en el formulario de nueva propiedad.");
            model.addAttribute("properties",  propertyRepository.findAll());
            model.addAttribute("types", Property.Type.values());
            model.addAttribute("statuses", Property.Status.values());
            return "property-form";
        }

        propertyRepository.save(property);
        logger.info("Propiedad {} insertada con éxito.", property.getCode());
        redirectAttributes.addFlashAttribute("successMessage", "Propiedad insertada correctamente.");
        return "redirect:/properties";
    }

    @PostMapping("/update")
    public String updateProperty(
            @Valid @ModelAttribute("property") Property property,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {
        logger.info("Actualizando propiedad con ID {}", property.getId());

        if(result.hasErrors()){
            logger.warn("Errores de validación al actualizar la propiedad.");
            model.addAttribute("properties",  propertyRepository.findAll());
            model.addAttribute("types", Property.Type.values());
            model.addAttribute("statuses", Property.Status.values());
            return "property-form";
        }

        propertyRepository.save(property);
        logger.info("Propiedad con ID {} actualizada con éxito.", property.getId());
        redirectAttributes.addFlashAttribute("successMessage", "Propiedad actualizada correctamente.");
        return "redirect:/properties";
    }

    @PostMapping("/delete")
    public String deleteProperty(
            @RequestParam("id") Long id,
            RedirectAttributes redirectAttributes) {
        logger.info("Eliminando propiedad con ID {}", id);
        propertyRepository.deleteById(id);
        logger.info("Propiedad con ID {} eliminada con éxito.", id);
        redirectAttributes.addFlashAttribute("successMessage", "Propiedad eliminada correctamente.");
        return "redirect:/properties";
    }
}
