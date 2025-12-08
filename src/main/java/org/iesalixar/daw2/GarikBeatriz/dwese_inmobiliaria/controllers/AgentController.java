package org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.controllers;

import jakarta.validation.Valid;
import org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.entities.Agent;
import org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.entities.Property;
import org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.entities.dto.AgentDTO;
import org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.repositories.AgentRepository;
import org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.repositories.AppointmentRepository;
import org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.repositories.OfficeRepository;
import org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.repositories.PropertyRepository;
import org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.services.FileStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
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
    private AppointmentRepository appointmentRepository;

    @Autowired
    private PropertyRepository propertyRepository;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private MessageSource messageSource;

    @GetMapping
    public String listAgents(
            @RequestParam(defaultValue = "1") int page,             // Página actual (por defecto la 1)
            @RequestParam(defaultValue = "") String keyword,        // Palabra clave de búsqueda (por defecto vacía)
            @RequestParam(defaultValue = "id") String sortBy,       // Campo por el que ordenar (por defecto 'id')
            @RequestParam(defaultValue = "asc") String direction,   // Dirección de ordenación (ascendente por defecto)
            Model model) {
        logger.info("Listing agents. Page: {}, Keyword: {}, Sort: {}, Dir: {}", page, keyword, sortBy, direction);

        // 1. Configuración de la Paginación y Ordenación
        int pageSize = 8;

        // Creamos el objeto Sort dependiendo de la dirección elegida
        Sort sort = direction.equalsIgnoreCase(Sort.Direction.ASC.name()) ?
                Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

        // Creamos el objeto Pageable. Restamos 1 a 'page' porque Spring Data cuenta páginas desde 0,
        // pero en la URL es más amigable empezar por 1.
        Pageable pageable = PageRequest.of(page - 1, pageSize, sort);
        Page<Agent> agentPage;

        // 2. Lógica de Búsqueda vs Listado Completo
        if (keyword == null || keyword.isEmpty()) {
            // Si no hay palabra clave, traemos todos los agentes paginados
            agentPage = agentRepository.findAll(pageable);
        } else {
            // Si hay palabra clave, usamos el metodo de búsqueda personalizado (por nombre, dni, email...)
            agentPage = agentRepository.searchAgents(keyword, pageable);
        }

        // 3. Empaquetado de datos (DTO)
        // Usamos un DTO para enviar la lista de agentes y la info de paginación a la vista de forma limpia
        AgentDTO agentDTO = new AgentDTO(
                agentPage.getContent(),
                agentPage.getTotalPages(),
                page
        );

        // Devolvemos estos parámetros a la vista para que los enlaces de paginación y ordenación
        // mantengan el estado actual (ej: si cambio de página, que no se pierda la búsqueda).
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
            String message = messageSource.getMessage("msg.agent.flash.not-found", null, LocaleContextHolder.getLocale());
            redirectAttributes.addFlashAttribute("errorMessage", message);
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
            @RequestParam(value = "propertyIds", required = false) List<Long> propertyIds,
            Model model,
            RedirectAttributes redirectAttributes
    ){
        if(result.hasErrors()){
            model.addAttribute("offices",  officeRepository.findAll());
            model.addAttribute("allProperties", propertyRepository.findAll());
            return "agent-form";
        }

        // 2. Validación de negocio: Verificar que el DNI no esté duplicado en la base de datos
        if(agentRepository.existsAgentByDni(agent.getDni())){
            String message = messageSource.getMessage("msg.agent.flash.dni-exists", null, LocaleContextHolder.getLocale());
            model.addAttribute("errorMessage", message);

            model.addAttribute("offices",  officeRepository.findAll());
            model.addAttribute("allProperties", propertyRepository.findAll());
            return "agent-form";
        }

        // 3. Gestión de la relación ManyToMany (Propiedades asignadas al agente)
        // Si el formulario envía IDs de propiedades, las buscamos y las asignamos.
        if (propertyIds != null) {
            List<Property> selectedProperties = propertyRepository.findAllById(propertyIds);
            agent.setProperties(selectedProperties);
        }

        // 4. Gestión de subida de imagen de perfil
        if(imageFile != null && !imageFile.isEmpty()){
            String fileName = fileStorageService.saveFile(imageFile);
            if(fileName != null){
                agent.setImage(fileName);
            }
        }

        agentRepository.save(agent);

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
        // 1. Manejo de errores de validación al actualizar
        if(result.hasErrors()){
            // Si hay error, el objeto 'agent' que viene del formulario no tiene la imagen antigua.
            // Debemos recuperarla de la BD para que, al volver a mostrar el formulario con errores,
            // no parezca que la foto se ha perdido (la vista necesita saber si hay imagen para mostrarla).
            if (agent.getId() != null) {
                Optional<Agent> dbAgent = agentRepository.findById(agent.getId());
                dbAgent.ifPresent(value -> agent.setImage(value.getImage()));
            }

            model.addAttribute("offices",  officeRepository.findAll());
            model.addAttribute("allProperties", propertyRepository.findAll());
            return "agent-form";
        }

        // 2. Validación de DNI duplicado (Excluyendo al propio agente que estamos editando)
        // Verificamos si existe otro agente con ese DNI que NO sea este mismo ID.
        if(agentRepository.existsAgentByDniAndIdNot(agent.getDni(), agent.getId())){
            if (agent.getId() != null) {
                Optional<Agent> agentOpt = agentRepository.findById(agent.getId());
                agentOpt.ifPresent(value -> agent.setImage(value.getImage()));
            }

            String message = messageSource.getMessage("msg.agent.flash.dni-exists", null, LocaleContextHolder.getLocale());
            model.addAttribute("errorMessage", message);

            model.addAttribute("offices",  officeRepository.findAll());
            model.addAttribute("allProperties", propertyRepository.findAll());
            return "agent-form";
        }

        // 3. Proceso de actualización
        // Primero recuperamos la entidad original de la base de datos (Persistente)
        Optional<Agent> existingOpt = agentRepository.findById(agent.getId());
        if(existingOpt.isPresent()){
            Agent existingAgent = existingOpt.get();

            // Actualizamos los campos básicos con los datos del formulario
            existingAgent.setName(agent.getName());
            existingAgent.setDni(agent.getDni());
            existingAgent.setPhone(agent.getPhone());
            existingAgent.setEmail(agent.getEmail());
            existingAgent.setOffice(agent.getOffice());

            // Actualizamos la lista de propiedades (Relación ManyToMany)
            if (propertyIds != null) {
                List<Property> selectedProperties = propertyRepository.findAllById(propertyIds);
                existingAgent.setProperties(selectedProperties);
            } else {
                existingAgent.setProperties(new ArrayList<>());
            }

            // Actualizamos la imagen SOLO si el usuario ha subido un fichero nuevo.
            // Si el campo viene vacío, mantenemos la imagen que ya tenía 'existingAgent'.
            if(imageFile != null && !imageFile.isEmpty()) {
                String fileName = fileStorageService.saveFile(imageFile);
                if (fileName != null) {
                    existingAgent.setImage(fileName);
                }
            }
            agentRepository.save(existingAgent);

            String message = messageSource.getMessage("msg.agent.flash.updated", null, LocaleContextHolder.getLocale());
            redirectAttributes.addFlashAttribute("successMessage", message);
        } else {
            String message = messageSource.getMessage("msg.agent.flash.not-found", null, LocaleContextHolder.getLocale());
            redirectAttributes.addFlashAttribute("errorMessage", message);
        }

        return "redirect:/agents";
    }

    @PostMapping("/delete")
    public String deleteAgent(
            @RequestParam("id") Long id,
            RedirectAttributes redirectAttributes
    ){
        logger.info("Eliminando agente con ID {}", id);

        if (appointmentRepository.existsByAgentId(id)) {
            logger.warn("El agente con ID {} no se puede eliminar porque tiene citas.", id);

            String message = messageSource.getMessage("msg.agent.flash.has-appointments", null, LocaleContextHolder.getLocale());
            redirectAttributes.addFlashAttribute("errorMessage", message);

            return "redirect:/agents";
        }

        agentRepository.deleteById(id);
        logger.info("Agente con ID {} eliminado correctamente", id);

        String message = messageSource.getMessage("msg.agent.flash.deleted", null, LocaleContextHolder.getLocale());
        redirectAttributes.addFlashAttribute("successMessage", message);
        return "redirect:/agents";
    }

    @PostMapping("/deleteImage")
    public String deleteAgentImage(
            @RequestParam("id") Long id,
            RedirectAttributes redirectAttributes){
        Optional<Agent> agentOpt = agentRepository.findById(id);

        // Verificar si el agente existe y si tiene una imagen asignada
        if (agentOpt.isPresent() && agentOpt.get().getImage() != null) {
            // Borrado Físico: Eliminar el archivo del disco duro (carpeta 'uploads')
            fileStorageService.deleteFile(agentOpt.get().getImage());

            // Borrado Lógico: Poner el campo 'image' a null en la base de datos
            agentOpt.get().setImage(null);
            agentRepository.save(agentOpt.get());

            String message = messageSource.getMessage("msg.agent.flash.image-deleted", null, LocaleContextHolder.getLocale());
            redirectAttributes.addFlashAttribute("successMessage", message);
        } else {
            String message = messageSource.getMessage("msg.agent.flash.image-not-found", null, LocaleContextHolder.getLocale());
            redirectAttributes.addFlashAttribute("errorMessage", message);
        }
        // Redirección: Volvemos al formulario de edición de ESTE mismo agente
        // para que el usuario vea que la foto ha desaparecido.
        return "redirect:/agents/edit?id=" + id;
    }

    // Redirecciones de seguridad (get methods for post actions)
    @GetMapping("/update")
    public String redirectLostUpdate(@RequestParam(required = false) Long id) {
        if (id != null) return "redirect:/agents/edit?id=" + id;
        return "redirect:/agents";
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