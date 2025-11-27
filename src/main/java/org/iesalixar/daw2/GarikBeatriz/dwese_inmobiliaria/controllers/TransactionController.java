package org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.controllers;

import jakarta.validation.Valid;
import org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.entities.*;
import org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.repositories.AgentRepository;
import org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.repositories.ClientRepository;
import org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.repositories.PropertyRepository;
import org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.repositories.TransactionRepository;
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
@RequestMapping("/transactions")
public class TransactionController {
    private static final Logger logger = LoggerFactory.getLogger(TransactionController.class);

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private PropertyRepository propertyRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private AgentRepository agentRepository;

    @GetMapping
    public String listTransactions(Model model) {
        logger.info("Solicitando la lista de todas las transacciones...");
        List<Transaction> listTransactions = transactionRepository.findAll();
        logger.info("Se han cargado {} transaciiones.", listTransactions.size());
        model.addAttribute("listTransactions", listTransactions);
        model.addAttribute("activePage", "transactions");
        return "transaction";
    }

    @GetMapping("/new")
    public String showNewForm(Model model) {
        logger.info("Solicitando formulario para nueva transacción...");
        model.addAttribute("transaction", new Transaction());

        List<Property> properties = propertyRepository.findAll();
        model.addAttribute("properties", properties);

        List<Client> clients = clientRepository.findAll();
        model.addAttribute("clients", clients);

        List<Agent> agents = agentRepository.findAll();
        model.addAttribute("agents", agents);

        return "transaction-form";
    }

    @GetMapping("/edit")
    public String showEditForm(
            @RequestParam("id") Long id,
            Model model,
            RedirectAttributes redirectAttributes
    ){
        logger.info("Solicitando formulario para editar transaciión con ID {}", id);
        Optional<Transaction> transactionOpt = transactionRepository.findById(id);

        if(transactionOpt.isEmpty()){
            logger.warn("No se encontró la transacción con ID {}", id);
            redirectAttributes.addFlashAttribute("message", "Transacción no encontrada");
            return "redirect:/transactions";
        }

        model.addAttribute("transaction", transactionOpt.get());
        model.addAttribute("properties",  propertyRepository.findAll());
        model.addAttribute("clients", clientRepository.findAll());
        model.addAttribute("agents", agentRepository.findAll());
        return "transaction-form";
    }

    @PostMapping("/insert")
    public String insertTransaction(
            @Valid @ModelAttribute("transaction") Transaction transaction,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes
    ){
        logger.info("Insertando nuevo transaccion con ID {}", transaction.getId());

        if(result.hasErrors()){
            logger.warn("Errores de validación en el formulario de nueva transaccion.");
            model.addAttribute("properties",  propertyRepository.findAll());
            model.addAttribute("clients", clientRepository.findAll());
            model.addAttribute("agents", agentRepository.findAll());
            return "transaction-form";
        }

        if(transactionRepository.existsTransactionByCode(transaction.getCode())){
            logger.warn("Transaccion con código {} ya existe", transaction.getCode());
            redirectAttributes.addFlashAttribute("errorMessage", "El código de la transaccion ya existe");
            model.addAttribute("properties",  propertyRepository.findAll());
            model.addAttribute("clients", clientRepository.findAll());
            model.addAttribute("agents", agentRepository.findAll());
            return "transaction-form";
        }

        transactionRepository.save(transaction);
        logger.info("Transaccion {} insertado con éxito", transaction.getCode());
        redirectAttributes.addFlashAttribute("successMessage", "Transaccion insertada correctamente.");
        return "redirect:/transactions";
    }

    @PostMapping("/update")
    public String updateAgent(
            @Valid @ModelAttribute("transaction") Transaction transaction,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes
    ){
        logger.info("Actualizando transaccion con ID {}", transaction.getId());

        if(result.hasErrors()){
            logger.warn("Errores de validación al actualizar la transaccion.");
            model.addAttribute("properties",  propertyRepository.findAll());
            model.addAttribute("clients", clientRepository.findAll());
            model.addAttribute("agents", agentRepository.findAll());
            return "transaction-form";
        }

        if(transactionRepository.existsTransactionByCode(transaction.getCode())){
            logger.warn("El código de la transaccion {} ya existe para otra transaccion.", transaction.getCode());
            model.addAttribute("errorMessage", "El código de la transaccion ya existe para otra transaccion.");
            model.addAttribute("properties",  propertyRepository.findAll());
            model.addAttribute("clients", clientRepository.findAll());
            model.addAttribute("agents", agentRepository.findAll());
            return "transaction-form";
        }

        transactionRepository.save(transaction);
        logger.info("Transaccion con ID {} actualizada correctamente", transaction.getId());
        redirectAttributes.addFlashAttribute("successMessage", "Transaccion actualizada correctamente.");

        return "redirect:/transactions";
    }

    @PostMapping("/delete")
    public String deleteAgent(
            @RequestParam("id") Long id,
            RedirectAttributes redirectAttributes
    ){
        logger.info("Eliminando transaccion con ID {}", id);
        transactionRepository.deleteById(id);
        logger.info("Transaccion con ID {} eliminada correctamente", id);
        redirectAttributes.addFlashAttribute("successMessage", "Transaccion eliminada correctamente.");
        return "redirect:/transactions";
    }
}
