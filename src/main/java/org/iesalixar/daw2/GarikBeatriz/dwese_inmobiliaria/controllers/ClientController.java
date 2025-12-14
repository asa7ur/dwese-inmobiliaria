package org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.controllers;

import jakarta.validation.Valid;
import org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.entities.Client;
import org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.entities.dto.ClientDTO;
import org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.services.ClientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/clients")
public class ClientController {
    private static final Logger logger = LoggerFactory.getLogger(ClientController.class);

    @Autowired
    private ClientService clientService;

    @Autowired
    private MessageSource messageSource;

    @GetMapping
    public String listClients(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction,
            Model model) {

        logger.info("Listing clients via Service. Page: {}", page);

        ClientDTO clientDTO = clientService.listClients(page, 6, sortBy, direction, keyword);

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
        Optional<Client> clientOpt = clientService.findById(id);

        if(clientOpt.isEmpty()){
            String message = messageSource.getMessage("msg.client.flash.not-found", null, LocaleContextHolder.getLocale());
            redirectAttributes.addFlashAttribute("errorMessage", message);
            return "redirect:/clients";
        }

        model.addAttribute("client", clientOpt.get());
        return "client-form";
    }

    @PostMapping("/insert")
    public String insertClient(
            @Valid @ModelAttribute("client") Client client,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes
    ){
        if(result.hasErrors()){
            return "client-form";
        }

        if(clientService.existsByDni(client.getDni())){
            String message = messageSource.getMessage("msg.client.flash.dni-exists", null, LocaleContextHolder.getLocale());
            model.addAttribute("errorMessage", message);
            return "client-form";
        }

        clientService.saveClient(client);

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
        if(result.hasErrors()){
            return "client-form";
        }

        if(clientService.existsByDniAndIdNot(client.getDni(), client.getId())){
            String message = messageSource.getMessage("msg.client.flash.dni-exists", null, LocaleContextHolder.getLocale());
            model.addAttribute("errorMessage", message);
            return "client-form";
        }

        Client updated = clientService.updateClient(client);

        if (updated != null) {
            String message = messageSource.getMessage("msg.client.flash.updated", null, LocaleContextHolder.getLocale());
            redirectAttributes.addFlashAttribute("successMessage", message);
        } else {
            // Caso raro donde el ID no existe
            String message = messageSource.getMessage("msg.client.flash.not-found", null, LocaleContextHolder.getLocale());
            redirectAttributes.addFlashAttribute("errorMessage", message);
        }

        return "redirect:/clients";
    }

    @PostMapping("/delete")
    public String deleteClient(@RequestParam("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            clientService.deleteClient(id);
            String message = messageSource.getMessage("msg.client.flash.deleted", null, LocaleContextHolder.getLocale());
            redirectAttributes.addFlashAttribute("successMessage", message);
        } catch (Exception e) {
            // Capturamos la excepción de negocio (cliente con citas)
            String message = messageSource.getMessage(e.getMessage(), null, LocaleContextHolder.getLocale());
            redirectAttributes.addFlashAttribute("errorMessage", message);
        }
        return "redirect:/clients";
    }

    // Redirecciones de seguridad
    @GetMapping("/update")
    public String redirectLostUpdate(@RequestParam(required = false) Long id) {
        return (id != null) ? "redirect:/clients/edit?id=" + id : "redirect:/clients";
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