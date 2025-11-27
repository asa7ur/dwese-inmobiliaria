package org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.controllers;

import org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.entities.Property;
import org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.repositories.PropertyRepository;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
        return "property-form";
    }

    @GetMapping("/edit")
    public String showEditForm(@RequestParam("id") Long id, Model model) {
        logger.info("Mostrando formulario de edición para la propiedad con ID {}", id);
        Optional<Property> propertyOpt = propertyRepository.findById(id);
        if (propertyOpt.isEmpty()) {
            logger.warn("No se encontró la propiedad con ID {}", id);
        }
        model.addAttribute("property", propertyOpt);
        return "property-form";
    }

    @PostMapping("/insert")
    public String insertProperty(@ModelAttribute("property") Property property) {
        logger.info("Insertando nueva propiedad con código {}", property.getCode());
        propertyRepository.save(property);
        logger.info("Propiedad {} insertada con éxito.", property.getCode());
        return "redirect:/properties";
    }

    @PostMapping("/update")
    public String updateProperty(@ModelAttribute("property") Property property) {
        logger.info("Actualizando propiedad con ID {}", property.getId());
        propertyRepository.save(property);
        logger.info("Propiedad con ID {} actualizada con éxito.", property.getId());
        return "redirect:/properties";
    }

    @PostMapping("/delete")
    public String deleteProperty(@RequestParam("id") Long id) {
        logger.info("Eliminando propiedad con ID {}", id);
        propertyRepository.deleteById(id);
        logger.info("Propiedad con ID {} eliminada con éxito.", id);
        return "redirect:/properties";
    }
}
