package org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.controllers;

import jakarta.validation.Valid;
import org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.entities.Property;
import org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.entities.dto.PropertyDTO;
import org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.repositories.AgentRepository;
import org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.repositories.PropertyRepository;

import org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.services.FileStorageService;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    private PropertyRepository propertyRepository;

    @Autowired
    private AgentRepository agentRepository;

    @Autowired
    private FileStorageService fileStorageService;

    @GetMapping
    public String listProperties(@RequestParam(defaultValue = "1") int page,
                                 @RequestParam(defaultValue = "") String keyword,
                                 @RequestParam(defaultValue = "id") String sortBy,
                                 @RequestParam(defaultValue = "asc") String direction,
                                 Model model) {
        logger.info("Listing propertiess. Page: {}, Keyword: {}, Sort: {}, Dir: {}", page, keyword, sortBy, direction);

        int pageSize = 6;

        Sort sort = direction.equalsIgnoreCase(Sort.Direction.ASC.name()) ?
                Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page - 1, pageSize, sort);

        Page<Property> propertyPage;
        if (keyword == null || keyword.isEmpty()) {
            propertyPage = propertyRepository.findAll(pageable);
        } else {
            propertyPage = propertyRepository.searchProperties(keyword, pageable);
        }

        PropertyDTO propertyDTO = new PropertyDTO(
                propertyPage.getContent(),
                propertyPage.getTotalPages(),
                page
        );

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
        logger.info("Solicitando formulario para editar propiedad con ID {}", id);
        Optional<Property> propertyOpt = propertyRepository.findById(id);

        if (propertyOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Propiedad no encontrada");
            return "redirect:/properties";
        }

        model.addAttribute("property", propertyOpt.get());
        model.addAttribute("agents", agentRepository.findAll());
        model.addAttribute("types", Property.Type.values());
        model.addAttribute("statuses", Property.Status.values());

        model.addAttribute("allAgents", agentRepository.findAll());
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
            model.addAttribute("types", Property.Type.values());
            model.addAttribute("statuses", Property.Status.values());
            return "property-form";
        }

        fileStorageService.processImages(property, files);

        propertyRepository.save(property);
        redirectAttributes.addFlashAttribute("successMessage", "Propiedad insertada correctamente.");
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
            model.addAttribute("types", Property.Type.values());
            model.addAttribute("statuses", Property.Status.values());
            return "property-form";
        }
        Optional<Property> existingOpt = propertyRepository.findById(property.getId());

        if (existingOpt.isPresent()) {
            Property existingProperty = existingOpt.get();

            existingProperty.setName(property.getName());
            existingProperty.setDescription(property.getDescription());
            existingProperty.setLocation(property.getLocation());
            existingProperty.setPrice(property.getPrice());
            existingProperty.setType(property.getType());
            existingProperty.setFloors(property.getFloors());
            existingProperty.setBedrooms(property.getBedrooms());
            existingProperty.setBathrooms(property.getBathrooms());
            existingProperty.setStatus(property.getStatus());

            fileStorageService.processImages(existingProperty, files);

            propertyRepository.save(existingProperty);

            redirectAttributes.addFlashAttribute("successMessage", "Propiedad actualizada correctamente.");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "No se encontró la propiedad.");
        }

        return "redirect:/properties";
    }

    @PostMapping("/delete")
    public String deleteProperty(@RequestParam("id") Long id, RedirectAttributes redirectAttributes) {
        propertyRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("successMessage", "Propiedad eliminada correctamente.");
        return "redirect:/properties";
    }

    @PostMapping("/delete-image")
    public String deleteImage(
            @RequestParam("imageId") Long imageId,
            @RequestParam("propertyId") Long propertyId,
            RedirectAttributes redirectAttributes) {

        Optional<Property> propertyOpt = propertyRepository.findById(propertyId);
        if (propertyOpt.isPresent()) {
            Property property = propertyOpt.get();

            // Buscar la imagen en la lista y eliminarla (orphanRemoval se encargará del resto al guardar)
            property.getImages().removeIf(img -> img.getId().equals(imageId));
            propertyRepository.save(property);

            redirectAttributes.addFlashAttribute("successMessage", "Imagen eliminada.");
        }

        return "redirect:/properties/edit?id=" + propertyId;
    }
}