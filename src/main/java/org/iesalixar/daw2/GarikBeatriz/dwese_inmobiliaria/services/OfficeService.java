package org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.services;

import org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.entities.Office;
import org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.entities.dto.OfficeDTO;
import org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.repositories.AgentRepository;
import org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.repositories.OfficeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class OfficeService {

    @Autowired
    private OfficeRepository officeRepository;

    @Autowired
    private AgentRepository agentRepository;

    public OfficeDTO listOffices(int page, int size, String sortBy, String direction, String keyword) {
        Sort sort = direction.equalsIgnoreCase(Sort.Direction.ASC.name()) ?
                Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page - 1, size, sort);
        Page<Office> officePage;

        if (keyword == null || keyword.isEmpty()) {
            officePage = officeRepository.findAll(pageable);
        } else {
            officePage = officeRepository.searchOffices(keyword, pageable);
        }

        return new OfficeDTO(
                officePage.getContent(),
                officePage.getTotalPages(),
                page
        );
    }

    public List<Office> findAll() {
        return officeRepository.findAll();
    }

    public Optional<Office> findById(Long id) {
        return officeRepository.findById(id);
    }

    @Transactional
    public void saveOffice(Office office) {
        officeRepository.save(office);
    }

    @Transactional
    public Office updateOffice(Office office) {
        if (office.getId() != null && officeRepository.existsById(office.getId())) {
            return officeRepository.save(office);
        }
        return null;
    }

    @Transactional
    public void deleteOffice(Long id) throws Exception {
        // Regla de negocio: No borrar si tiene agentes asignados
        if (agentRepository.existsByOfficeId(id)) {
            throw new Exception("msg.office.flash.has-agents");
        }
        officeRepository.deleteById(id);
    }
}