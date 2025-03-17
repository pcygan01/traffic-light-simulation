package com.avsystem.trafficlight.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Command to execute a step in the simulation.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class StepCommand extends Command {
    // No additional properties needed
} 