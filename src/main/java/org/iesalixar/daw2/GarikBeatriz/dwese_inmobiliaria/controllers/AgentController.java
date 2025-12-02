package org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.controllers;

import jakarta.validation.Valid;
import org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.entities.Agent;
import org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.entities.Property;
import org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.entities.dto.AgentDTO;
import org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.repositories.AgentRepository;
import org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.repositories.OfficeRepository;
import org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.repositories.PropertyRepository;
import org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.services.FileStorageService;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/agents")
public class AgentController {
    private static final Logger logger = LoggerFactory.getLogger(AgentController.class);

    @Autowired
    private AgentRepository agentRepository;

    @Autowired
    private OfficeRepository officeRepository;

    @Autowired
    PropertyRepository propertyRepository;

    @Autowired
    private FileStorageService fileStorageService;

    @GetMapping
    public String listAgents(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction,
            Model model) {
        logger.info("Listing agents. Page: {}, Keyword: {}, Sort: {}, Dir: {}", page, keyword, sortBy, direction);

        int pageSize = 8;

        Sort sort = direction.equalsIgnoreCase(Sort.Direction.ASC.name()) ?
                Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page - 1, pageSize, sort);

        Page<Agent> agentPage;
        if (keyword == null || keyword.isEmpty()) {
            agentPage = agentRepository.findAll(pageable);
        } else {
            agentPage = agentRepository.searchAgents(keyword, pageable);
        }

        AgentDTO agentDTO = new AgentDTO(
                agentPage.getContent(),
                agentPage.getTotalPages(),
                page
        );

        logger.info("Se han cargado {} agentes.", agentDTO.getAgents().size());
        model.addAttribute("listAgents", agentDTO);
        model.addAttribute("keyword", keyword);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("direction", direction);
        model.addAttribute("reverseSortDir", direction.equals("asc") ? "desc" : "asc");
        model.addAttribute("activePage", "agents");
        return "agent";
    }

    @GetMapping("/new")
    public String showNewForm(Model model) {
        logger.info("Solicitando formulario para nuevo agente...");
        model.addAttribute("agent", new Agent());
        model.addAttribute("offices", officeRepository.findAll());
        model.addAttribute("allProperties", propertyRepository.findAll());

        return "agent-form";
    }

    @GetMapping("/edit")
    public String showEditForm(
            @RequestParam("id") Long id,
            Model model,
            RedirectAttributes redirectAttributes
    ){
        logger.info("Solicitando formulario para editar agente con ID {}", id);
        Optional<Agent> agentOpt = agentRepository.findById(id);

        if(agentOpt.isEmpty()){
            redirectAttributes.addFlashAttribute("message", "Agente no encontrado");
            return "redirect:/agents";
        }

        model.addAttribute("agent", agentOpt.get());
        model.addAttribute("offices",  officeRepository.findAll());

        model.addAttribute("allProperties", propertyRepository.findAll());

        return "agent-form";
    }

    @PostMapping("/insert")
    public String insertAgent(
            @Valid @ModelAttribute("agent") Agent agent,
            BindingResult result,
            @RequestParam("imageFile") MultipartFile imageFile,
            @RequestParam(value = "propertyIds", required = false) List<Long> propertyIds, // AGREGADO
            Model model,
            RedirectAttributes redirectAttributes
    ){
        if(result.hasErrors()){
            model.addAttribute("offices",  officeRepository.findAll());
            model.addAttribute("allProperties", propertyRepository.findAll()); // Recargar lista si hay error
            return "agent-form";
        }

        if(agentRepository.existsAgentByDni(agent.getDni())){
            redirectAttributes.addFlashAttribute("errorMessage", "El Dni del agente ya existe");
            model.addAttribute("offices",  officeRepository.findAll());
            model.addAttribute("allProperties", propertyRepository.findAll());
            return "agent-form";
        }

        if (propertyIds != null) {
            List<Property> selectedProperties = propertyRepository.findAllById(propertyIds);
            agent.setProperties(selectedProperties);
        } else {
            agent.setProperties(new ArrayList<>());
        }

        if(imageFile != null && !imageFile.isEmpty()){
            String fileName = fileStorageService.saveFile(imageFile);
            if(fileName != null){
                agent.setImage(fileName);
            }
        }

        agentRepository.save(agent);
        redirectAttributes.addFlashAttribute("successMessage", "Agente insertado correctamente.");
        return "redirect:/agents";
    }

    @PostMapping("/update")
    public String updateAgent(
            @Valid @ModelAttribute("agent") Agent agent,
            BindingResult result,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
            @RequestParam(value = "propertyIds", required = false) List<Long> propertyIds, // AGREGADO
            Model model,
            RedirectAttributes redirectAttributes
    ){
        if(result.hasErrors()){
            model.addAttribute("offices",  officeRepository.findAll());
            model.addAttribute("allProperties", propertyRepository.findAll());
            return "agent-form";
        }

        if(agentRepository.existsAgentByDniAndIdNot(agent.getDni(), agent.getId())){
            model.addAttribute("errorMessage", "El DNI ya existe.");
            model.addAttribute("offices",  officeRepository.findAll());
            model.addAttribute("allProperties", propertyRepository.findAll());
            return "agent-form";
        }

        if (propertyIds != null) {
            List<Property> selectedProperties = propertyRepository.findAllById(propertyIds);
            agent.setProperties(selectedProperties);
        } else {
            agent.setProperties(new ArrayList<>());
        }

        if(imageFile != null && !imageFile.isEmpty()){
            String fileName = fileStorageService.saveFile(imageFile);
            if(fileName != null){
                agent.setImage(fileName);
            }
        }

        agentRepository.save(agent);

        redirectAttributes.addFlashAttribute("successMessage", "Agente actualizado correctamente.");
        return "redirect:/agents";
    }

    @PostMapping("/delete")
    public String deleteAgent(
            @RequestParam("id") Long id,
            RedirectAttributes redirectAttributes
    ){
        logger.info("Eliminando agente con ID {}", id);
        agentRepository.deleteById(id);
        logger.info("Agente con ID {} eliminado correctamente", id);
        redirectAttributes.addFlashAttribute("successMessage", "Agente eliminado correctamente.");
        return "redirect:/agents";
    }

    @PostMapping("/deleteImage")
    public String deleteAgentImage(
            @RequestParam("id") Long id,
            RedirectAttributes redirectAttributes){
        Optional<Agent> agentOpt = agentRepository.findById(id);
        if (agentOpt.isPresent() && agentOpt.get().getImage() != null) {
            fileStorageService.deleteFile(agentOpt.get().getImage());
            agentOpt.get().setImage(null);
            agentRepository.save(agentOpt.get());
            redirectAttributes.addFlashAttribute("successMessage", "Imagen del agente eliminada correctamente.");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "No se encontró imagen para eliminar.");
        }
        return "redirect:/agents/edit?id=" + id;
    }

}