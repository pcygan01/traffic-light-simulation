package com.avsystem.trafficlight.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;

/**
 * Base class for all commands that can be executed in the simulation.
 */
@Data
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    property = "type"
)
@JsonSubTypes({
    @JsonSubTypes.Type(value = AddVehicleCommand.class, name = "addVehicle"),
    @JsonSubTypes.Type(value = StepCommand.class, name = "step")
})
public abstract class Command {
    private String type;
} 