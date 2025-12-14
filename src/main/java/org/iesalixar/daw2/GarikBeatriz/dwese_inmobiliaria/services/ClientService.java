package org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.services;

import org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.entities.Client;
import org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.entities.dto.ClientDTO;
import org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.repositories.AppointmentRepository;
import org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.repositories.ClientRepository;
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
public class ClientService {

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    public ClientDTO listClients(int page, int size, String sortBy, String direction, String keyword) {
        Sort sort = direction.equalsIgnoreCase(Sort.Direction.ASC.name()) ?
                Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page - 1, size, sort);
        Page<Client> clientPage;

        if (keyword == null || keyword.isEmpty()) {
            clientPage = clientRepository.findAll(pageable);
        } else {
            clientPage = clientRepository.searchClients(keyword, pageable);
        }

        return new ClientDTO(
                clientPage.getContent(),
                clientPage.getTotalPages(),
                page
        );
    }

    public Optional<Client> findById(Long id) {
        return clientRepository.findById(id);
    }

    @Transactional
    public void saveClient(Client client) {
        clientRepository.save(client);
    }

    @Transactional
    public Client updateClient(Client client) {
        if (client.getId() != null && clientRepository.existsById(client.getId())) {
            return clientRepository.save(client);
        }
        return null;
    }

    @Transactional
    public void deleteClient(Long id) throws Exception {
        // Regla de negocio: No borrar si tiene citas pendientes
        if (appointmentRepository.existsByClientId(id)) {
            throw new Exception("msg.client.flash.has-appointments");
        }

        // No borrar si tiene transacciones
        if (transactionRepository.existsByClientId(id)) {
            throw new Exception("msg.client.flash.has-transactions");
        }

        clientRepository.deleteById(id);
    }

    public boolean existsByDni(String dni) {
        return clientRepository.existsClientByDni(dni);
    }

    public boolean existsByDniAndIdNot(String dni, Long id) {
        return clientRepository.existsClientByDniAndIdNot(dni, id);
    }
}