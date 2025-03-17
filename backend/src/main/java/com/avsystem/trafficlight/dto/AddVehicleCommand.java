package com.avsystem.trafficlight.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Command to add a vehicle to the simulation.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AddVehicleCommand extends Command {
    private String vehicleId;
    private String startRoad;
    private String endRoad;
} 