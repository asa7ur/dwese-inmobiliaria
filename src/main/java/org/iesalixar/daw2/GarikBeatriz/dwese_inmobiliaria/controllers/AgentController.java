package org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.controllers;

import jakarta.validation.Valid;
import org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.entities.Agent;
import org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.entities.dto.AgentDTO;
import org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.repositories.OfficeRepository;
import org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.repositories.PropertyRepository;
import org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.services.AgentService;
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

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/agents")
public class AgentController {
    private static final Logger logger = LoggerFactory.getLogger(AgentController.class);

    @Autowired
    private AgentService agentService;

    @Autowired
    private OfficeRepository officeRepository;

    @Autowired
    private PropertyRepository propertyRepository;

    @Autowired
    private MessageSource messageSource;

    @GetMapping
    public String listAgents(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction,
            Model model) {

        AgentDTO agentDTO = agentService.listAgents(page, 8, sortBy, direction, keyword);

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
        model.addAttribute("agent", new Agent());
        model.addAttribute("offices", officeRepository.findAll());
        model.addAttribute("allProperties", propertyRepository.findAll());
        return "agent-form";
    }

    @GetMapping("/edit")
    public String showEditForm(@RequestParam("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Agent> agentOpt = agentService.findById(id);

        if (agentOpt.isEmpty()) {
            String message = messageSource.getMessage("msg.agent.flash.not-found", null, LocaleContextHolder.getLocale());
            redirectAttributes.addFlashAttribute("errorMessage", message);
            return "redirect:/agents";
        }

        model.addAttribute("agent", agentOpt.get());
        model.addAttribute("offices", officeRepository.findAll());
        model.addAttribute("allProperties", propertyRepository.findAll());
        return "agent-form";
    }

    @PostMapping("/insert")
    public String insertAgent(
            @Valid @ModelAttribute("agent") Agent agent,
            BindingResult result,
            @RequestParam("imageFile") MultipartFile imageFile,
            @RequestParam(value = "propertyIds", required = false) List<Long> propertyIds,
            Model model,
            RedirectAttributes redirectAttributes
    ){
        if (result.hasErrors()) {
            model.addAttribute("offices", officeRepository.findAll());
            model.addAttribute("allProperties", propertyRepository.findAll());
            return "agent-form";
        }

        if (agentService.existsByDni(agent.getDni())) {
            String message = messageSource.getMessage("msg.agent.flash.dni-exists", null, LocaleContextHolder.getLocale());
            model.addAttribute("errorMessage", message);
            model.addAttribute("offices", officeRepository.findAll());
            model.addAttribute("allProperties", propertyRepository.findAll());
            return "agent-form";
        }

        agentService.saveAgent(agent, imageFile, propertyIds);

        String message = messageSource.getMessage("msg.agent.flash.created", null, LocaleContextHolder.getLocale());
        redirectAttributes.addFlashAttribute("successMessage", message);
        return "redirect:/agents";
    }

    @PostMapping("/update")
    public String updateAgent(
            @Valid @ModelAttribute("agent") Agent agent,
            BindingResult result,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
            @RequestParam(value = "propertyIds", required = false) List<Long> propertyIds,
            Model model,
            RedirectAttributes redirectAttributes
    ){
        if (result.hasErrors()) {
            // Si hay error, necesitamos recuperar la imagen antigua para mostrarla en la vista
            Optional<Agent> dbAgent = agentService.findById(agent.getId());
            dbAgent.ifPresent(value -> agent.setImage(value.getImage()));

            model.addAttribute("offices", officeRepository.findAll());
            model.addAttribute("allProperties", propertyRepository.findAll());
            return "agent-form";
        }

        if (agentService.existsByDniAndIdNot(agent.getDni(), agent.getId())) {
            Optional<Agent> agentOpt = agentService.findById(agent.getId());
            agentOpt.ifPresent(value -> agent.setImage(value.getImage()));

            String message = messageSource.getMessage("msg.agent.flash.dni-exists", null, LocaleContextHolder.getLocale());
            model.addAttribute("errorMessage", message);
            model.addAttribute("offices", officeRepository.findAll());
            model.addAttribute("allProperties", propertyRepository.findAll());
            return "agent-form";
        }

        Agent updated = agentService.updateAgent(agent, imageFile, propertyIds);

        if (updated != null) {
            String message = messageSource.getMessage("msg.agent.flash.updated", null, LocaleContextHolder.getLocale());
            redirectAttributes.addFlashAttribute("successMessage", message);
        } else {
            String message = messageSource.getMessage("msg.agent.flash.not-found", null, LocaleContextHolder.getLocale());
            redirectAttributes.addFlashAttribute("errorMessage", message);
        }

        return "redirect:/agents";
    }

    @PostMapping("/delete")
    public String deleteAgent(@RequestParam("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            agentService.deleteAgent(id);
            String message = messageSource.getMessage("msg.agent.flash.deleted", null, LocaleContextHolder.getLocale());
            redirectAttributes.addFlashAttribute("successMessage", message);
        } catch (Exception e) {
            // Si el servicio lanza una excepción (ej: tiene citas), mostramos el mensaje de error
            String messageKey = e.getMessage(); // "msg.agent.flash.has-appointments"
            String message = messageSource.getMessage(messageKey, null, LocaleContextHolder.getLocale());
            redirectAttributes.addFlashAttribute("errorMessage", message);
        }
        return "redirect:/agents";
    }

    @PostMapping("/deleteImage")
    public String deleteAgentImage(@RequestParam("id") Long id, RedirectAttributes redirectAttributes){
        // Verificamos existencia antes de intentar borrar
        Optional<Agent> agentOpt = agentService.findById(id);

        if (agentOpt.isPresent() && agentOpt.get().getImage() != null) {
            agentService.deleteAgentImage(id);
            String message = messageSource.getMessage("msg.agent.flash.image-deleted", null, LocaleContextHolder.getLocale());
            redirectAttributes.addFlashAttribute("successMessage", message);
        } else {
            String message = messageSource.getMessage("msg.agent.flash.image-not-found", null, LocaleContextHolder.getLocale());
            redirectAttributes.addFlashAttribute("errorMessage", message);
        }
        return "redirect:/agents/edit?id=" + id;
    }

    @GetMapping("/update")
    public String redirectLostUpdate(@RequestParam(required = false) Long id) {
        return (id != null) ? "redirect:/agents/edit?id=" + id : "redirect:/agents";
    }

    @GetMapping("/insert")
    public String redirectLostInsert() {
        return "redirect:/agents/new";
    }

    @GetMapping({"/delete", "/delete-image"})
    public String redirectLostDelete() {
        return "redirect:/agents";
    }
}