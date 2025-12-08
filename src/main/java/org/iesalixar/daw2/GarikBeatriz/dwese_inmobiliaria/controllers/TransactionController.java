package org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.controllers;

import jakarta.validation.Valid;
import org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.components.AgentPropertyValidator;
import org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.entities.*;
import org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.entities.dto.TransactionDTO;
import org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.repositories.AgentRepository;
import org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.repositories.ClientRepository;
import org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.repositories.PropertyRepository;
import org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.repositories.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource; // IMPORTANTE
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

import java.time.LocalDate;
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
        logger.info("Listing transactions. Page: {}, Keyword: {}, Sort: {}, Dir: {}", page, keyword, sortBy, direction);

        int pageSize = 5;

        Sort sort = direction.equalsIgnoreCase(Sort.Direction.ASC.name()) ?
                Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page - 1, pageSize, sort);

        Page<Transaction> transactionPage;
        if (keyword == null || keyword.isEmpty()) {
            transactionPage = transactionRepository.findAll(pageable);
        } else {
            transactionPage = transactionRepository.searchTransactions(keyword, pageable);
        }

        TransactionDTO transactionDTO = new TransactionDTO(
                transactionPage.getContent(),
                transactionPage.getTotalPages(),
                page
        );

        logger.info("Se han cargado {} transacciones.", transactionDTO.getTransactions().size());
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
        logger.info("Solicitando formulario para nueva transacción...");

        Transaction transaction = new Transaction();
        transaction.setTransactionDate(LocalDate.now());

        model.addAttribute("transaction", transaction);
        model.addAttribute("properties", propertyRepository.findAll());
        model.addAttribute("clients", clientRepository.findAll());
        model.addAttribute("agents", agentRepository.findAll());

        return "transaction-form";
    }

    @GetMapping("/edit")
    public String showEditForm(
            @RequestParam("id") Long id,
            Model model,
            RedirectAttributes redirectAttributes
    ){
        logger.info("Solicitando formulario para editar transacción con ID {}", id);
        Optional<Transaction> transactionOpt = transactionRepository.findById(id);

        if(transactionOpt.isEmpty()){
            logger.warn("No se encontró la transacción con ID {}", id);

            String message = messageSource.getMessage("msg.transaction.flash.not-found", null, LocaleContextHolder.getLocale());
            redirectAttributes.addFlashAttribute("errorMessage", message);
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
        logger.info("Insertando nueva transacción");

        if (transaction.getProperty() != null && transactionRepository.existsByPropertyId(transaction.getProperty().getId())) {
            result.rejectValue("property", "msg.transaction.error.property-busy", "Esta propiedad ya tiene una transacción asociada activa.");
        }

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

        if(result.hasErrors()){
            logger.warn("Errores de validación en el formulario de nueva transacción.");
            model.addAttribute("properties",  propertyRepository.findAll());
            model.addAttribute("clients", clientRepository.findAll());
            model.addAttribute("agents", agentRepository.findAll());
            return "transaction-form";
        }

        transactionRepository.save(transaction);
        logger.info("Transacción {} insertada con éxito", transaction.getCode());

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
        logger.info("Actualizando transacción con ID {}", transaction.getId());

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

        if(result.hasErrors()){
            logger.warn("Errores de validación al actualizar la transacción.");
            model.addAttribute("properties",  propertyRepository.findAll());
            model.addAttribute("clients", clientRepository.findAll());
            model.addAttribute("agents", agentRepository.findAll());
            return "transaction-form";
        }

        transactionRepository.save(transaction);
        logger.info("Transacción con ID {} actualizada correctamente", transaction.getId());

        String message = messageSource.getMessage("msg.transaction.flash.updated", null, LocaleContextHolder.getLocale());
        redirectAttributes.addFlashAttribute("successMessage", message);

        return "redirect:/transactions";
    }

    @PostMapping("/delete")
    public String deleteTransaction(
            @RequestParam("id") Long id,
            RedirectAttributes redirectAttributes
    ){
        logger.info("Eliminando transacción con ID {}", id);
        transactionRepository.deleteById(id);
        logger.info("Transacción con ID {} eliminada correctamente", id);

        String message = messageSource.getMessage("msg.transaction.flash.deleted", null, LocaleContextHolder.getLocale());
        redirectAttributes.addFlashAttribute("successMessage", message);
        return "redirect:/transactions";
    }

    // Redirecciones de seguridad (get methods for post actions)
    @GetMapping("/update")
    public String redirectLostUpdate(@RequestParam(required = false) Long id) {
        if (id != null) return "redirect:/transactions/edit?id=" + id;
        return "redirect:/transactions";
    }

    @GetMapping("/insert")
    public String redirectLostInsert() {
        return "redirect:/transactions/new";
    }

    @GetMapping({"/delete", "/delete-image"})
    public String redirectLostDelete() {
        return "redirect:/transactions";
    }
}