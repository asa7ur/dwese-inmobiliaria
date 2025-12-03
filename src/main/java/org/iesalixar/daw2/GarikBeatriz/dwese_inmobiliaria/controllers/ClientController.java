package org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.controllers;

import jakarta.validation.Valid;
import org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.entities.Client;
import org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.entities.dto.ClientDTO;
import org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.repositories.ClientRepository;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/clients")
public class ClientController {
    private static final Logger logger =  LoggerFactory.getLogger(ClientController.class);

    @Autowired
    private ClientRepository clientRepository;

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
            logger.warn("No se encontro el cliente con ID {}", id);
            redirectAttributes.addFlashAttribute("message", "Cliente no encontrado");
            return "redirect:/clients";
        }

        model.addAttribute("client", clientOpt.get());
        return "client-form";
    }

    @PostMapping("/insert")
    public String insertClient(
            @Valid @ModelAttribute("agent") Client client,
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
            redirectAttributes.addFlashAttribute("message", "El cliente ya existe");
            return "client-form";
        }

        clientRepository.save(client);
        logger.info("Cliente con DNI {} insertado con éxito", client.getDni());
        redirectAttributes.addFlashAttribute("message", "Cliente insertado");
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
            model.addAttribute("message", "El DNI del cliente ya existe");
            return "client-form";
        }

        clientRepository.save(client);
        logger.info("Cliente con ID {} actualizado correctamente", client.getId());
        redirectAttributes.addFlashAttribute("message", "Cliente actualizado correctamente");

        return "redirect:/clients";
    }

    @PostMapping("/delete")
    public String deleteAgent(
            @RequestParam("id") Long id,
            RedirectAttributes redirectAttributes
    ){
        logger.info("Eliminando cliente con ID {}", id);
        clientRepository.deleteById(id);
        logger.info("Cliente con ID {} eliminado correctamente", id);
        redirectAttributes.addFlashAttribute("message", "Cliente eliminado correctamente");
        return "redirect:/clients";
    }
}
