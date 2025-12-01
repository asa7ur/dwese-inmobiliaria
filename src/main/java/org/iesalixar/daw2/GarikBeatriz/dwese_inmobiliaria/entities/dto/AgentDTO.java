package org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.entities.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.entities.Agent;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AgentDTO {
    private List<Agent> agents;
    private int pages;
    private int currentPage;
}
