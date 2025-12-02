package org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.controllers;

import org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.entities.Office;
import org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.entities.dto.OfficeDTO;
import org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.repositories.OfficeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/offices")
public class OfficeController {
    private static final Logger logger = LoggerFactory.getLogger(OfficeController.class);

    @Autowired
    private OfficeRepository officeRepository;

    @GetMapping
    public String listOffices(@RequestParam(defaultValue = "1") int page,
                              @RequestParam(defaultValue = "") String keyword,
                              @RequestParam(defaultValue = "id") String sortBy,
                              @RequestParam(defaultValue = "asc") String direction,
                              Model model) {
        logger.info("Listing offices. Page: {}, Keyword: {}, Sort: {}, Dir: {}", page, keyword, sortBy, direction);

        int pageSize = 5;

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
            redirectAttributes.addFlashAttribute("errorMessage", "Sucursal no encontrado");
            logger.warn("No se encontró la sucursal con ID {}", id);
            return "redirect:/offices";
        }

        model.addAttribute("office", officeOpt.get());
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
