package org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.controllers;

import jakarta.validation.Valid;
import org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.entities.Client;
import org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.repositories.ClientRepository;
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
@RequestMapping("/clients")
public class ClientController {
    private static final Logger logger =  LoggerFactory.getLogger(ClientController.class);

    @Autowired
    private ClientRepository clientRepository;

    @GetMapping
    public String listClients(Model model) {
        logger.info("Solicitando lista de clientes");
        List<Client> listClients = clientRepository.findAll();
        logger.info("Se han cargado {} clientes", listClients.size());
        model.addAttribute("listClients", listClients);
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

        if(clientRepository.existsClientByCode(client.getCode())){
            logger.warn("Existe un cliente con el código {}", client.getCode());
            redirectAttributes.addFlashAttribute("message", "El cliente ya existe");
            return "client-form";
        }

        clientRepository.save(client);
        logger.info("Cliente {} insertado con éxito", client.getCode());
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

        if(clientRepository.existsClientByCode(client.getCode())){
            logger.warn("Existe un cliente con el codigo {}", client.getCode());
            model.addAttribute("message", "El código del cliente ya existe");
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
