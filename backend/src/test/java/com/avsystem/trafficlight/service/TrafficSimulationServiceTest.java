package com.avsystem.trafficlight.service;

import com.avsystem.trafficlight.dto.AddVehicleCommand;
import com.avsystem.trafficlight.dto.Command;
import com.avsystem.trafficlight.dto.CommandList;
import com.avsystem.trafficlight.dto.SimulationResult;
import com.avsystem.trafficlight.dto.StepCommand;
import com.avsystem.trafficlight.dto.StepStatus;
import com.avsystem.trafficlight.model.Direction;
import com.avsystem.trafficlight.model.TrafficLight;
import com.avsystem.trafficlight.model.TrafficLightState;
import com.avsystem.trafficlight.model.Turn;
import com.avsystem.trafficlight.model.Vehicle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the TrafficSimulationService class
 */
public class TrafficSimulationServiceTest {
    
    private TrafficSimulationService simulationService;
    
    @BeforeEach
    void setUp() {
        simulationService = new TrafficSimulationService();
        simulationService.initializeSimulation();
    }
    
    @Test
    void testInitializeSimulation() {
        // Simulation should be properly initialized
        Map<Direction, TrafficLight> trafficLights = simulationService.getTrafficLightStates();
        assertNotNull(trafficLights);
        assertEquals(4, trafficLights.size());
        
        // Initially, N-S direction should have green lights
        assertTrue(trafficLights.get(Direction.NORTH).getMainState() == TrafficLightState.GREEN);
        assertTrue(trafficLights.get(Direction.SOUTH).getMainState() == TrafficLightState.GREEN);
        assertTrue(trafficLights.get(Direction.EAST).getMainState() == TrafficLightState.RED);
        assertTrue(trafficLights.get(Direction.WEST).getMainState() == TrafficLightState.RED);
    }
    
    @Test
    void testAddVehicle() {
        // Add a vehicle going straight from NORTH to SOUTH
        AddVehicleCommand command = new AddVehicleCommand();
        command.setVehicleId("vehicle1");
        command.setStartRoad("NORTH");
        command.setEndRoad("SOUTH");
        
        simulationService.addVehicle(command);
        
        // Check that the vehicle was added to the NORTH queue
        Map<Direction, Integer> counts = simulationService.getWaitingVehicleCounts();
        assertEquals(1, counts.get(Direction.NORTH));
        assertEquals(0, counts.get(Direction.SOUTH));
        assertEquals(0, counts.get(Direction.EAST));
        assertEquals(0, counts.get(Direction.WEST));
    }
    
    @Test
    void testExecuteStep_StraightVehicleWithGreenLight() {
        // Add a vehicle going straight from NORTH to SOUTH (has green light)
        AddVehicleCommand command = new AddVehicleCommand();
        command.setVehicleId("vehicle1");
        command.setStartRoad("NORTH");
        command.setEndRoad("SOUTH");
        
        simulationService.addVehicle(command);
        
        // Execute a step
        StepStatus status = simulationService.executeStep();
        
        // The vehicle should have left the intersection
        assertTrue(status.getLeftVehicles().contains("vehicle1"));
        
        // No more vehicles should be waiting at NORTH
        Map<Direction, Integer> counts = simulationService.getWaitingVehicleCounts();
        assertEquals(0, counts.get(Direction.NORTH));
    }
    
    @Test
    void testExecuteStep_StraightVehicleWithRedLight() {
        // Add a vehicle going straight from EAST to WEST (has red light initially)
        // Also add a vehicle on the green axis to prevent immediate phase switch
        AddVehicleCommand command1 = new AddVehicleCommand();
        command1.setVehicleId("vehicleNorth");
        command1.setStartRoad("NORTH");
        command1.setEndRoad("SOUTH");
        simulationService.addVehicle(command1);
        
        AddVehicleCommand command2 = new AddVehicleCommand();
        command2.setVehicleId("vehicleEast");
        command2.setStartRoad("EAST");
        command2.setEndRoad("WEST");
        simulationService.addVehicle(command2);
        
        // Execute a step - the North vehicle should leave, but East vehicle should wait
        StepStatus status = simulationService.executeStep();
        
        // The North vehicle should have left
        assertTrue(status.getLeftVehicles().contains("vehicleNorth"));
        
        // The East vehicle should NOT have left
        assertFalse(status.getLeftVehicles().contains("vehicleEast"));
        
        // The East vehicle should still be waiting
        Map<Direction, Integer> counts = simulationService.getWaitingVehicleCounts();
        assertEquals(0, counts.get(Direction.NORTH));
        assertEquals(1, counts.get(Direction.EAST));
    }
    
    @Test
    void testRunSimulation() {
        // Create a simulation with vehicles on both axes to test phase switching
        List<Command> commands = new ArrayList<>();
        
        // Add a vehicle on NORTH (green light)
        AddVehicleCommand vehicleCommand1 = new AddVehicleCommand();
        vehicleCommand1.setVehicleId("vehicle1");
        vehicleCommand1.setStartRoad("NORTH");
        vehicleCommand1.setEndRoad("SOUTH");
        commands.add(vehicleCommand1);
        
        // Add a vehicle on EAST (red light) in the same step
        AddVehicleCommand vehicleCommand2 = new AddVehicleCommand();
        vehicleCommand2.setVehicleId("vehicle2");
        vehicleCommand2.setStartRoad("EAST");
        vehicleCommand2.setEndRoad("WEST");
        commands.add(vehicleCommand2);
        
        // Execute first step (North vehicle should leave)
        commands.add(new StepCommand());
        
        // Execute second step (East vehicle should leave after phase switch)
        commands.add(new StepCommand());
        
        CommandList commandList = new CommandList();
        commandList.setCommands(commands);
        
        // Run the simulation
        SimulationResult result = simulationService.runSimulation(commandList);
        
        // Check that we have the correct number of steps
        assertEquals(2, result.getStepStatuses().size());
        
        // First step should have vehicle1 leaving
        assertTrue(result.getStepStatuses().get(0).getLeftVehicles().contains("vehicle1"));
        assertFalse(result.getStepStatuses().get(0).getLeftVehicles().contains("vehicle2"));
        
        // Second step should have vehicle2 leaving (after phase switch)
        assertTrue(result.getStepStatuses().get(1).getLeftVehicles().contains("vehicle2"));
    }
} 