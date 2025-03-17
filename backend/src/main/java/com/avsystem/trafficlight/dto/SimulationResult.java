package com.avsystem.trafficlight.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Class representing the complete result of a simulation.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SimulationResult {
    private List<StepStatus> stepStatuses;
} 