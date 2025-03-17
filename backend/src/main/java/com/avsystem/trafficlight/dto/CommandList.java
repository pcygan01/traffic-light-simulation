package com.avsystem.trafficlight.dto;

import java.util.List;
import lombok.Data;

/**
 * Class representing a list of commands from the input JSON file.
 */
@Data
public class CommandList {
    private List<Command> commands;
} 