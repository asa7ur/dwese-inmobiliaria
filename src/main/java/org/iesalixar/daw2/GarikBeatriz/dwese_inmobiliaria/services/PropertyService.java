package org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.services;

import org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.entities.Agent;
import org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.entities.Property;
import org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.entities.dto.PropertyDTO;
import org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.repositories.AgentRepository;
import org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.repositories.AppointmentRepository;
import org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.repositories.PropertyRepository;
import org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.repositories.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Service
public class PropertyService {

    @Autowired
    private PropertyRepository propertyRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private AgentRepository agentRepository;

    @Autowired
    private FileStorageService fileStorageService;

    public PropertyDTO listProperties(int page, int size, String sortBy, String direction, String keyword) {
        Sort sort = direction.equalsIgnoreCase(Sort.Direction.ASC.name()) ?
                Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page - 1, size, sort);
        Page<Property> propertyPage;

        if (keyword == null || keyword.isEmpty()) {
            propertyPage = propertyRepository.findAll(pageable);
        } else {
            propertyPage = propertyRepository.searchProperties(keyword, pageable);
        }

        return new PropertyDTO(
                propertyPage.getContent(),
                propertyPage.getTotalPages(),
                page
        );
    }

    public List<Property> findAll() {
        return propertyRepository.findAll();
    }

    public Optional<Property> findById(Long id) {
        return propertyRepository.findById(id);
    }

    @Transactional
    public void saveProperty(Property property, MultipartFile[] files) {
        // Procesar y guardar imágenes físicas + referencias en BD
        fileStorageService.processImages(property, files);
        propertyRepository.save(property);
    }

    @Transactional
    public Property updateProperty(Property propertyInput, MultipartFile[] files) {
        Optional<Property> existingOpt = propertyRepository.findById(propertyInput.getId());

        if (existingOpt.isPresent()) {
            Property existingProperty = existingOpt.get();

            // Actualización manual de campos para preservar relaciones no incluidas en el formulario
            existingProperty.setName(propertyInput.getName());
            existingProperty.setDescription(propertyInput.getDescription());
            existingProperty.setLocation(propertyInput.getLocation());
            existingProperty.setPrice(propertyInput.getPrice());
            existingProperty.setType(propertyInput.getType());
            existingProperty.setFloors(propertyInput.getFloors());
            existingProperty.setBedrooms(propertyInput.getBedrooms());
            existingProperty.setBathrooms(propertyInput.getBathrooms());
            existingProperty.setStatus(propertyInput.getStatus());

            // Añadir nuevas imágenes a la lista existente
            fileStorageService.processImages(existingProperty, files);

            return propertyRepository.save(existingProperty);
        }
        return null;
    }

    @Transactional
    public void deleteProperty(Long id) throws Exception {
        // 1. Validaciones de negocio
        if (transactionRepository.existsByPropertyId(id)) {
            throw new Exception("msg.property.flash.has-transaction");
        }

        if (appointmentRepository.existsByPropertyId(id)) {
            throw new Exception("msg.property.flash.has-appointments");
        }

        Optional<Property> propertyOpt = propertyRepository.findById(id);
        if (propertyOpt.isPresent()) {
            Property property = propertyOpt.get();

            // 2. Desvincular agentes (Limpiar tabla intermedia property_agent)
            // Es necesario guardar el agente porque él es el "owner" de la relación ManyToMany
            for (Agent agent : property.getAgents()) {
                agent.getProperties().remove(property);
                agentRepository.save(agent);
            }

            // 3. Eliminar imágenes del disco (opcional, pero recomendado para no dejar basura)
            property.getImages().forEach(img -> fileStorageService.deleteFile(img.getFileName()));

            // 4. Eliminar propiedad
            propertyRepository.delete(property);
        }
    }

    @Transactional
    public void deletePropertyImage(Long propertyId, Long imageId) {
        Optional<Property> propertyOpt = propertyRepository.findById(propertyId);
        if (propertyOpt.isPresent()) {
            Property property = propertyOpt.get();

            // Buscar la imagen para borrar el fichero físico antes de borrar el registro
            property.getImages().stream()
                    .filter(img -> img.getId().equals(imageId))
                    .findFirst()
                    .ifPresent(img -> fileStorageService.deleteFile(img.getFileName()));

            // Eliminar de la lista y guardar (CascadeType.ALL se encarga del resto)
            property.getImages().removeIf(img -> img.getId().equals(imageId));
            propertyRepository.save(property);
        }
    }
}