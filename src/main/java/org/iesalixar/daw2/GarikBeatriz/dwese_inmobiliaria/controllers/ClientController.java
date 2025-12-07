package org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.controllers;

import jakarta.validation.Valid;
import org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.entities.Client;
import org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.entities.dto.ClientDTO;
import org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.repositories.AppointmentRepository;
import org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.repositories.ClientRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder; // IMPORTANTE
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/clients")
public class ClientController {
    private static final Logger logger =  LoggerFactory.getLogger(ClientController.class);

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private MessageSource messageSource;

    @GetMapping
    public String listClients(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction,
            Model model) {
        logger.info("Listing clients. Page: {}, Keyword: {}, Sort: {}, Dir: {}", page, keyword, sortBy, direction);

        int pageSize = 6;
        Sort sort = direction.equalsIgnoreCase(Sort.Direction.ASC.name()) ?
                Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page -1, pageSize, sort);
        Page<Client> clientPage;

        if (keyword == null || keyword.isEmpty()){
            clientPage = clientRepository.findAll(pageable);
        } else {
            clientPage = clientRepository.searchClients(keyword, pageable);
        }

        ClientDTO clientDTO = new ClientDTO(
                clientPage.getContent(),
                clientPage.getTotalPages(),
                page
        );

        logger.info("Se han cargado {} clientes", clientDTO.getClients().size());
        model.addAttribute("listClients", clientDTO);
        model.addAttribute("keyword", keyword);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("direction", direction);
        model.addAttribute("reverseSortDir", direction.equals("asc") ? "desc" : "asc");
        model.addAttribute("activePage", "clients");
        return "client";
    }

    @GetMapping("/new")
    public String showNewForm(Model model) {
        logger.info("Solicitando formulario para nuevo cliente...");
        model.addAttribute("client", new Client());
        return "client-form";
    }

    @GetMapping("/edit")
    public String showEditForm(
            @RequestParam("id") Long id,
            Model model,
            RedirectAttributes redirectAttributes
    ){
        logger.info("Solicitando formulario para editar cliente con ID {}", id);
        Optional<Client> clientOpt = clientRepository.findById(id);

        if(clientOpt.isEmpty()){
            logger.warn("No se encontró el cliente con ID {}", id);

            String message = messageSource.getMessage("msg.client.flash.not-found", null, LocaleContextHolder.getLocale());
            redirectAttributes.addFlashAttribute("errorMessage", message);
            return "redirect:/clients";
        }

        model.addAttribute("client", clientOpt.get());
        return "client-form";
    }

    @PostMapping("/insert")
    public String insertClient(
            @Valid @ModelAttribute("client") Client client, // CORREGIDO: antes ponía "agent"
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes
    ){
        logger.info("Insertando nuevo cliente con ID {}", client.getId());

        if(result.hasErrors()){
            logger.warn("Errores de validación en el formulario de nuevo cliente");
            return "client-form";
        }

        if(clientRepository.existsClientByDni(client.getDni())){
            logger.warn("Existe un cliente con el DNI {}", client.getDni());

            String message = messageSource.getMessage("msg.client.flash.dni-exists", null, LocaleContextHolder.getLocale());
            model.addAttribute("errorMessage", message);
            return "client-form";
        }

        clientRepository.save(client);
        logger.info("Cliente con DNI {} insertado con éxito", client.getDni());

        String message = messageSource.getMessage("msg.client.flash.created", null, LocaleContextHolder.getLocale());
        redirectAttributes.addFlashAttribute("successMessage", message);
        return "redirect:/clients";
    }

    @PostMapping("/update")
    public String updateClient(
            @Valid @ModelAttribute("client") Client client,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes
    ){
        logger.info("Actualizando cliente con ID {}", client.getId());

        if(result.hasErrors()){
            logger.warn("Errores de validación al actualizar cliente");
            return "client-form";
        }

        if(clientRepository.existsClientByDniAndIdNot(client.getDni(), client.getId())){
            logger.warn("El DNI del cliente {} ya existe.", client.getDni());

            String message = messageSource.getMessage("msg.client.flash.dni-exists", null, LocaleContextHolder.getLocale());
            model.addAttribute("errorMessage", message);
            return "client-form";
        }

        clientRepository.save(client);
        logger.info("Cliente con ID {} actualizado correctamente", client.getId());

        String message = messageSource.getMessage("msg.client.flash.updated", null, LocaleContextHolder.getLocale());
        redirectAttributes.addFlashAttribute("successMessage", message);

        return "redirect:/clients";
    }

    @PostMapping("/delete")
    public String deleteClient(
                                @RequestParam("id") Long id,
                                RedirectAttributes redirectAttributes
    ){
        logger.info("Eliminando cliente con ID {}", id);

        if (appointmentRepository.existsByClientId(id)) {
            logger.warn("El cliente con ID {} tiene citas pendientes y no se puede borrar.", id);

            String message = messageSource.getMessage("msg.client.flash.has-appointments", null, LocaleContextHolder.getLocale());
            redirectAttributes.addFlashAttribute("errorMessage", message);

            return "redirect:/clients";
        }

        clientRepository.deleteById(id);
        logger.info("Cliente con ID {} eliminado correctamente", id);

        String message = messageSource.getMessage("msg.client.flash.deleted", null, LocaleContextHolder.getLocale());
        redirectAttributes.addFlashAttribute("successMessage", message);
        return "redirect:/clients";
    }

    // Redirecciones de seguridad (get methods for post actions)
    @GetMapping("/update")
    public String redirectLostUpdate(@RequestParam(required = false) Long id) {
        if (id != null) return "redirect:/clients/edit?id=" + id;
        return "redirect:/clients";
    }

    @GetMapping("/insert")
    public String redirectLostInsert() {
        return "redirect:/clients/new";
    }

    @GetMapping({"/delete"})
    public String redirectLostDelete() {
        return "redirect:/clients";
    }
}