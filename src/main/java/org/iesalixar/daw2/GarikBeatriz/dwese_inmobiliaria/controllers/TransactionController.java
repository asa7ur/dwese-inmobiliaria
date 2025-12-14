package org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.controllers;

import jakarta.validation.Valid;
import org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.components.AgentPropertyValidator;
import org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.entities.*;
import org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.entities.dto.TransactionDTO;
import org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.repositories.AgentRepository;
import org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.repositories.ClientRepository;
import org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.repositories.PropertyRepository;
import org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.services.TransactionService;
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

import java.time.LocalDate;
import java.util.Optional;

@Controller
@RequestMapping("/transactions")
public class TransactionController {
    private static final Logger logger = LoggerFactory.getLogger(TransactionController.class);

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private PropertyRepository propertyRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private AgentRepository agentRepository;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private AgentPropertyValidator agentPropertyValidator;

    @GetMapping
    public String listTransactions(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "transactionTimestamp") String sortBy,
            @RequestParam(defaultValue = "desc") String direction,
            Model model) {

        logger.info("Listando transacciones via Service. Page: {}", page);

        TransactionDTO transactionDTO = transactionService.listTransactions(page, 5, sortBy, direction, keyword);

        model.addAttribute("listTransactions", transactionDTO);
        model.addAttribute("keyword", keyword);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("direction", direction);
        model.addAttribute("reverseSortDir", direction.equals("asc") ? "desc" : "asc");
        model.addAttribute("activePage", "transactions");
        return "transaction";
    }

    @GetMapping("/new")
    public String showNewForm(Model model) {
        Transaction transaction = new Transaction();
        transaction.setTransactionDate(LocalDate.now());

        model.addAttribute("transaction", transaction);
        loadFormDependencies(model);
        return "transaction-form";
    }

    @GetMapping("/edit")
    public String showEditForm(
            @RequestParam("id") Long id,
            Model model,
            RedirectAttributes redirectAttributes
    ){
        Optional<Transaction> transactionOpt = transactionService.findById(id);

        if(transactionOpt.isEmpty()){
            String message = messageSource.getMessage("msg.transaction.flash.not-found", null, LocaleContextHolder.getLocale());
            redirectAttributes.addFlashAttribute("errorMessage", message);
            return "redirect:/transactions";
        }

        model.addAttribute("transaction", transactionOpt.get());
        loadFormDependencies(model);
        return "transaction-form";
    }

    @PostMapping("/insert")
    public String insertTransaction(
            @Valid @ModelAttribute("transaction") Transaction transaction,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes
    ){
        // 1. Validación de Negocio: Propiedad Ocupada
        if (transaction.getProperty() != null && transactionService.isPropertyBusy(transaction.getProperty().getId())) {
            result.rejectValue("property", "msg.transaction.error.property-busy", "Esta propiedad ya tiene una transacción activa.");
        }

        // 2. Validación de Negocio: Asignación Agente-Propiedad
        validateAgentProperty(transaction, result);

        if(result.hasErrors()){
            loadFormDependencies(model);
            return "transaction-form";
        }

        transactionService.saveTransaction(transaction);

        String message = messageSource.getMessage("msg.transaction.flash.created", null, LocaleContextHolder.getLocale());
        redirectAttributes.addFlashAttribute("successMessage", message);
        return "redirect:/transactions";
    }

    @PostMapping("/update")
    public String updateTransaction(
            @Valid @ModelAttribute("transaction") Transaction transaction,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes
    ){
        // En update NO comprobamos isPropertyBusy porque la transacción ya existe y "ocupa" la propiedad ella misma.

        validateAgentProperty(transaction, result);

        if(result.hasErrors()){
            loadFormDependencies(model);
            return "transaction-form";
        }

        Transaction updated = transactionService.updateTransaction(transaction);

        if (updated == null) {
            // Caso raro si el ID no existe
            return "redirect:/transactions";
        }

        String message = messageSource.getMessage("msg.transaction.flash.updated", null, LocaleContextHolder.getLocale());
        redirectAttributes.addFlashAttribute("successMessage", message);

        return "redirect:/transactions";
    }

    @PostMapping("/delete")
    public String deleteTransaction(
            @RequestParam("id") Long id,
            RedirectAttributes redirectAttributes
    ){
        try {
            transactionService.deleteTransaction(id);
            String message = messageSource.getMessage("msg.transaction.flash.deleted", null, LocaleContextHolder.getLocale());
            redirectAttributes.addFlashAttribute("successMessage", message);
        } catch (Exception e) {
            String message = messageSource.getMessage(e.getMessage(), null, LocaleContextHolder.getLocale());
            redirectAttributes.addFlashAttribute("errorMessage", message);
        }
        return "redirect:/transactions";
    }

    // --- Métodos Auxiliares ---

    private void loadFormDependencies(Model model) {
        model.addAttribute("properties", propertyRepository.findAll());
        model.addAttribute("clients", clientRepository.findAll());
        model.addAttribute("agents", agentRepository.findAll());
    }

    private void validateAgentProperty(Transaction transaction, BindingResult result) {
        if (!result.hasFieldErrors("property") && !result.hasFieldErrors("agent")) {
            if (transaction.getProperty() != null && transaction.getAgent() != null) {
                agentPropertyValidator.validate(
                        transaction.getProperty().getId(),
                        transaction.getAgent().getId(),
                        result,
                        "agent"
                );
            }
        }
    }

    // Redirecciones de seguridad
    @GetMapping("/update")
    public String redirectLostUpdate(@RequestParam(required = false) Long id) {
        return (id != null) ? "redirect:/transactions/edit?id=" + id : "redirect:/transactions";
    }

    @GetMapping("/insert")
    public String redirectLostInsert() {
        return "redirect:/transactions/new";
    }

    @GetMapping({"/delete"})
    public String redirectLostDelete() {
        return "redirect:/transactions";
    }
}