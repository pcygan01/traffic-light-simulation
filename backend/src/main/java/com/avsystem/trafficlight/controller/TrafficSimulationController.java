package com.avsystem.trafficlight.controller;

import com.avsystem.trafficlight.dto.AddVehicleCommand;
import com.avsystem.trafficlight.dto.CommandList;
import com.avsystem.trafficlight.dto.SimulationResult;
import com.avsystem.trafficlight.dto.StepStatus;
import com.avsystem.trafficlight.model.*;
import com.avsystem.trafficlight.service.TrafficSimulationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class TrafficSimulationController {
    private final TrafficSimulationService simulationService;

    @Autowired
    public TrafficSimulationController(TrafficSimulationService simulationService) {
        this.simulationService = simulationService;
    }

    @PostMapping("/run-simulation")
    public ResponseEntity<SimulationResult> runSimulation(@RequestBody CommandList commandList) {
        System.out.println("Received request to run simulation with " + commandList.getCommands().size() + " commands");
        try {
            SimulationResult result = simulationService.runSimulation(commandList);
            System.out.println("Simulation completed with " + result.getStepStatuses().size() + " steps");
            System.out.println("First step contains " + (result.getStepStatuses().isEmpty() ? 0 : result.getStepStatuses().get(0).getLeftVehicles().size()) + " vehicles that left");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            System.err.println("Error running simulation: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
    
    @PostMapping("/initialize-simulation")
    public ResponseEntity<Void> initializeSimulation() {
        simulationService.initializeSimulation();
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/add-vehicle")
    public ResponseEntity<Void> addVehicle(@RequestBody AddVehicleCommand command) {
        simulationService.addVehicle(command);
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/execute-step")
    public ResponseEntity<StepStatus> executeStep() {
        StepStatus status = simulationService.executeStep();
        return ResponseEntity.ok(status);
    }
    
    @PostMapping("/set-right-turn-arrows")
    public ResponseEntity<Void> setRightTurnArrowsEnabled(@RequestParam boolean enabled) {
        simulationService.setRightTurnArrowsEnabled(enabled);
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/set-conditional-right-turn-arrows")
    public ResponseEntity<Void> setConditionalRightTurnArrowsEnabled(@RequestParam boolean enabled) {
        simulationService.setConditionalRightTurnArrowsEnabled(enabled);
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/set-max-vehicles")
    public ResponseEntity<Void> setMaxVehiclesToProcess(@RequestParam int maxVehicles) {
        simulationService.setMaxVehiclesToProcess(maxVehicles);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/traffic-light-states")
    public ResponseEntity<Map<String, TrafficLightInfo>> getTrafficLightStates() {
        Map<Direction, TrafficLight> trafficLights = simulationService.getTrafficLightStates();
        
        // Convert to a format suitable for the frontend
        Map<String, TrafficLightInfo> result = new HashMap<>();
        for (Map.Entry<Direction, TrafficLight> entry : trafficLights.entrySet()) {
            TrafficLight light = entry.getValue();
            TrafficLightInfo info = new TrafficLightInfo(
                light.getMainState(), 
                light.getRightTurnArrow()
            );
            result.put(entry.getKey().toString(), info);
        }
        
        return ResponseEntity.ok(result);
    }

    @GetMapping("/waiting-vehicles")
    public ResponseEntity<Map<String, Integer>> getWaitingVehicleCounts() {
        Map<Direction, Integer> waitingVehicles = simulationService.getWaitingVehicleCounts();
        
        // Convert to a format suitable for the frontend
        Map<String, Integer> result = new HashMap<>();
        for (Map.Entry<Direction, Integer> entry : waitingVehicles.entrySet()) {
            result.put(entry.getKey().toString(), entry.getValue());
        }
        
        return ResponseEntity.ok(result);
    }
} 