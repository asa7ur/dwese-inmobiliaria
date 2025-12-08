package org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.components;

import org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.repositories.PropertyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;

@Component
public class AgentPropertyValidator {

    @Autowired
    private PropertyRepository propertyRepository;

    @Autowired
    private MessageSource messageSource;

    /**
     * Valida si un agente está asignado a una propiedad.
     * Si no lo está, inyecta un error en el BindingResult.
     *
     * @param propertyId ID de la propiedad
     * @param agentId ID del agente
     * @param result El objeto BindingResult del formulario actual
     * @param fieldName El nombre del campo en el formulario (normalmente "agent")
     */
    public void validate(Long propertyId, Long agentId, BindingResult result, String fieldName) {
        if (propertyId != null && agentId != null) {

            boolean isAssigned = propertyRepository.isAgentAssignedToProperty(propertyId, agentId);

            if (!isAssigned) {
                String errorMsg = messageSource.getMessage(
                        "msg.appointment.agent.not-assigned",
                        null,
                        LocaleContextHolder.getLocale()
                );

                // Rechazamos el valor en el campo especificado
                result.rejectValue(fieldName, "error.agent", errorMsg);
            }
        }
    }
}