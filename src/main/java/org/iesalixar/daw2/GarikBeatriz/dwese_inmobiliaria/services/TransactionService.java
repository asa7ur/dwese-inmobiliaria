package org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.services;

import org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.entities.Transaction;
import org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.entities.dto.TransactionDTO;
import org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.repositories.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    public TransactionDTO listTransactions(int page, int size, String sortBy, String direction, String keyword) {
        Sort sort = direction.equalsIgnoreCase(Sort.Direction.ASC.name()) ?
                Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page - 1, size, sort);
        Page<Transaction> transactionPage;

        if (keyword == null || keyword.isEmpty()) {
            transactionPage = transactionRepository.findAll(pageable);
        } else {
            transactionPage = transactionRepository.searchTransactions(keyword, pageable);
        }

        return new TransactionDTO(
                transactionPage.getContent(),
                transactionPage.getTotalPages(),
                page
        );
    }

    public Optional<Transaction> findById(Long id) {
        return transactionRepository.findById(id);
    }

    @Transactional
    public Transaction saveTransaction(Transaction transaction) {
        return transactionRepository.save(transaction);
    }

    @Transactional
    public Transaction updateTransaction(Transaction transaction) {
        if (transaction.getId() != null && transactionRepository.existsById(transaction.getId())) {
            return transactionRepository.save(transaction);
        }
        return null;
    }

    @Transactional
    public void deleteTransaction(Long id) throws Exception {
        Optional<Transaction> transactionOpt = transactionRepository.findById(id);

        if (transactionOpt.isPresent()) {
            Transaction transaction = transactionOpt.get();

            // IMPORTANTE: Romper la relación bidireccional con Property antes de borrar.
            // Si la propiedad sigue apuntando a esta transacción, Hibernate podría lanzar error.
            if (transaction.getProperty() != null) {
                transaction.getProperty().setTransaction(null);
            }

            transactionRepository.delete(transaction);
        } else {
            throw new Exception("msg.transaction.flash.not-found");
        }
    }

    // --- Validaciones ---

    public boolean isPropertyBusy(Long propertyId) {
        return transactionRepository.existsByPropertyId(propertyId);
    }
}