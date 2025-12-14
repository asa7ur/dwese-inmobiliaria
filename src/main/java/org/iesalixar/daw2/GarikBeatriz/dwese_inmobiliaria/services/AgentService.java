package org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.services;

import org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.entities.Agent;
import org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.entities.Property;
import org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.entities.dto.AgentDTO;
import org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.repositories.AgentRepository;
import org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.repositories.AppointmentRepository;
import org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.repositories.PropertyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AgentService {

    @Autowired
    private AgentRepository agentRepository;

    @Autowired
    private PropertyRepository propertyRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private FileStorageService fileStorageService;

    public AgentDTO listAgents(int page, int size, String sortBy, String direction, String keyword) {
        Sort sort = direction.equalsIgnoreCase(Sort.Direction.ASC.name()) ?
                Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page - 1, size, sort);
        Page<Agent> agentPage;

        if (keyword == null || keyword.isEmpty()) {
            agentPage = agentRepository.findAll(pageable);
        } else {
            agentPage = agentRepository.searchAgents(keyword, pageable);
        }

        return new AgentDTO(
                agentPage.getContent(),
                agentPage.getTotalPages(),
                page
        );
    }

    public Optional<Agent> findById(Long id) {
        return agentRepository.findById(id);
    }

    @Transactional
    public void saveAgent(Agent agent, MultipartFile imageFile, List<Long> propertyIds) {
        // Asignar propiedades (Relación ManyToMany)
        if (propertyIds != null) {
            List<Property> selectedProperties = propertyRepository.findAllById(propertyIds);
            agent.setProperties(selectedProperties);
        }

        // Guardar imagen si existe
        if (imageFile != null && !imageFile.isEmpty()) {
            String fileName = fileStorageService.saveFile(imageFile);
            if (fileName != null) {
                agent.setImage(fileName);
            }
        }

        agentRepository.save(agent);
    }

    @Transactional
    public Agent updateAgent(Agent agentInput, MultipartFile imageFile, List<Long> propertyIds) {
        Optional<Agent> existingOpt = agentRepository.findById(agentInput.getId());

        if (existingOpt.isPresent()) {
            Agent existingAgent = existingOpt.get();

            // Actualizar datos básicos
            existingAgent.setName(agentInput.getName());
            existingAgent.setDni(agentInput.getDni());
            existingAgent.setPhone(agentInput.getPhone());
            existingAgent.setEmail(agentInput.getEmail());
            existingAgent.setOffice(agentInput.getOffice());

            // Actualizar propiedades
            if (propertyIds != null) {
                List<Property> selectedProperties = propertyRepository.findAllById(propertyIds);
                existingAgent.setProperties(selectedProperties);
            } else {
                existingAgent.setProperties(new ArrayList<>());
            }

            // Actualizar imagen (solo si viene una nueva)
            if (imageFile != null && !imageFile.isEmpty()) {
                // Borramos la imagen antigua para no dejar basura
                if (existingAgent.getImage() != null) {
                    fileStorageService.deleteFile(existingAgent.getImage());
                }

                String fileName = fileStorageService.saveFile(imageFile);
                if (fileName != null) {
                    existingAgent.setImage(fileName);
                }
            }

            return agentRepository.save(existingAgent);
        }
        return null;
    }

    @Transactional
    public void deleteAgent(Long id) throws Exception {
        // Validación de negocio: No borrar si tiene citas
        if (appointmentRepository.existsByAgentId(id)) {
            throw new Exception("msg.agent.flash.has-appointments");
        }

        // Borrar imagen física si tiene
        Optional<Agent> agent = agentRepository.findById(id);
        if (agent.isPresent() && agent.get().getImage() != null) {
            fileStorageService.deleteFile(agent.get().getImage());
        }

        agentRepository.deleteById(id);
    }

    @Transactional
    public void deleteAgentImage(Long id) {
        Optional<Agent> agentOpt = agentRepository.findById(id);
        if (agentOpt.isPresent() && agentOpt.get().getImage() != null) {
            fileStorageService.deleteFile(agentOpt.get().getImage());
            agentOpt.get().setImage(null);
            agentRepository.save(agentOpt.get());
        }
    }

    public boolean existsByDni(String dni) {
        return agentRepository.existsAgentByDni(dni);
    }

    public boolean existsByDniAndIdNot(String dni, Long id) {
        return agentRepository.existsAgentByDniAndIdNot(dni, id);
    }
}