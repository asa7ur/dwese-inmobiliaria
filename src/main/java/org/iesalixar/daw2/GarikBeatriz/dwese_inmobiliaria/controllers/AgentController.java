package org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.controllers;

import jakarta.validation.Valid;
import org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.entities.Agent;
import org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.entities.Office;
import org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.repositories.AgentRepository;
import org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.repositories.OfficeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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

    @GetMapping
    public String listAgents(Model model) {
        logger.info("Solicitando la lista de todos los agentes...");
        List<Agent> listAgents = agentRepository.findAll();
        logger.info("Se han cargado {} agentes.", listAgents.size());
        model.addAttribute("listAgents", listAgents);
        model.addAttribute("activePage", "agents");
        return "agent";
    }

    @GetMapping("/new")
    public String showNewForm(Model model) {
        logger.info("Solicitando formulario para nuevo agente...");
        model.addAttribute("agent", new Agent());

        List<Office> offices = officeRepository.findAll();
        model.addAttribute("offices", offices);

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
            logger.warn("No se encontró el agente con ID {}", id);
            redirectAttributes.addFlashAttribute("message", "Agente no encontrado");
            return "redirect:/agents";
        }

        model.addAttribute("agent", agentOpt.get());
        model.addAttribute("offices",  officeRepository.findAll());
        return "agent-form";
    }

    @PostMapping("/insert")
    public String insertAgent(
            @Valid @ModelAttribute("agent") Agent agent,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes
    ){
        logger.info("Insertando nuevo agente con ID {}", agent.getId());

        if(result.hasErrors()){
            logger.warn("Errores de validación en el formulario de nuevo agente.");
            model.addAttribute("offices",  officeRepository.findAll());
            return "agent-form";
        }

        if(agentRepository.existsAgentByCode(agent.getCode())){
            logger.warn("Agente con código {} ya existe",  agent.getCode());
            redirectAttributes.addFlashAttribute("errorMessage", "El código del agente ya existe");
            model.addAttribute("offices",  officeRepository.findAll());
            return "agent-form";
        }

        agentRepository.save(agent);
        logger.info("Agente {} insertado con éxito", agent.getCode());
        redirectAttributes.addFlashAttribute("successMessage", "Agente insertado correctamente.");
        return "redirect:/agents";
    }

    @PostMapping("/update")
    public String updateAgent(
            @Valid @ModelAttribute("agent") Agent agent,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes
    ){
        logger.info("Actualizando agente con ID {}", agent.getId());

        if(result.hasErrors()){
            logger.warn("Errores de validación al actualizar el agente.");
            model.addAttribute("offices",  officeRepository.findAll());
            return "agent-form";
        }

        if(agentRepository.existsAgentByCode(agent.getCode())){
            logger.warn("El código del agente {} ya existe para otro agente.", agent.getCode());
            model.addAttribute("errorMessage", "El código del agente ya existe para otro agente.");
            model.addAttribute("offices",  officeRepository.findAll());
            return "agent-form";
        }

        agentRepository.save(agent);
        logger.info("Agente con ID {} actualizado correctamente", agent.getId());
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

}
