package com.avsystem.trafficlight.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Class representing the status after a simulation step.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StepStatus {
    private List<String> leftVehicles;
} 