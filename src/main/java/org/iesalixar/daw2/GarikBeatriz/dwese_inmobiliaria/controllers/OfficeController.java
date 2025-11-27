package org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.controllers;

import org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.entities.Office;
import org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.repositories.OfficeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/office")
public class OfficeController {
    private static final Logger logger = LoggerFactory.getLogger(OfficeController.class);

    @Autowired
    private OfficeRepository officeRepository;

    @GetMapping
    public String listOffices(Model model) {
        logger.info("Solicitando la lista de todas las sucursales...");
        List<Office> listOffices = officeRepository.findAll();
        logger.info("Se han cargado {} sucursales.", listOffices.size());
        model.addAttribute("listOffices", listOffices);
        return "office";
    }

    @GetMapping("/new")
    public String showNewForm(Model model) {
        logger.info("Mostrando formulario para nueva sucursal.");
        model.addAttribute("office", new Office());
        return "office-form";
    }

    @GetMapping("/edit")
    public String showEditForm(@RequestParam("id") Long id, Model model) {
        logger.info("Mostrando formulario de edición para la sucursal con ID {}", id);
        Optional<Office> officeOpt = officeRepository.findById(id);
        if (officeOpt.isEmpty()) {
            logger.warn("No se encontró la sucursal con ID {}", id);
        }
        model.addAttribute("office", officeOpt);
        return "office-form";
    }

    @PostMapping("/insert")
    public String insertOffice(@ModelAttribute("office") Office office) {
        logger.info("Insertando nueva sucursal con código {}", office.getCode());
        officeRepository.save(office);
        logger.info("Sucursal {} insertada con éxito.", office.getCode());
        return "redirect:/offices";
    }

    @PostMapping("/update")
    public String updateOffice(@ModelAttribute("office") Office office) {
        logger.info("Actualizando sucursal con ID {}", office.getId());
        officeRepository.save(office);
        logger.info("Sucursal con ID {} actualizada con éxito.", office.getId());
        return "redirect:/offices";
    }

    @PostMapping("/delete")
    public String deleteOffice(@RequestParam("id") Long id) {
        logger.info("Eliminando sucursal con ID {}", id);
        officeRepository.deleteById(id);
        logger.info("Sucursal con ID {} eliminada con éxito.", id);
        return "redirect:/offices";
    }
}
