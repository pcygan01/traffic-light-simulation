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
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Service responsible for running the traffic simulation.
 */
@Service
public class TrafficSimulationService {

    private Map<Direction, Queue<Vehicle>> waitingVehicles;
    private Map<Direction, TrafficLight> trafficLights;
    private boolean rightTurnArrowsEnabled = false;
    private boolean conditionalRightTurnArrowsEnabled = false;
    private int maxVehiclesToProcess = 50;
    private int currentPhase = 0;
    private Direction[] phaseSequence = {Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};
    
    private int stepsInCurrentPhase = 0;
    
    private final int MIN_STEPS_PER_PHASE = 5;
    
    private Map<String, Integer> leftTurningVehiclesWaiting = new HashMap<>();
    

    public TrafficSimulationService() {
        initializeSimulation();
    }

    public void initializeSimulation() {
        waitingVehicles = new EnumMap<>(Direction.class);
        trafficLights = new EnumMap<>(Direction.class);
        
        currentPhase = 0;
        
        stepsInCurrentPhase = 0;
        
        leftTurningVehiclesWaiting.clear();
        
        for (Direction direction : Direction.values()) {
            waitingVehicles.put(direction, new LinkedList<>());
        }
        
        for (Direction direction : Direction.values()) {
            boolean isNorthSouthAxis = direction == Direction.NORTH || direction == Direction.SOUTH;
            boolean isGreen = isNorthSouthAxis; // Dla fazy 0: NORTH i SOUTH mają zielone
            
            trafficLights.put(direction, new TrafficLight(
                direction,
                isGreen ? TrafficLightState.GREEN : TrafficLightState.RED,
                rightTurnArrowsEnabled
            ));
        }
        
        System.out.println("Simulation initialized with phase " + currentPhase + 
                          " (NORTH-SOUTH axis has green light)");
    }

    public SimulationResult runSimulation(CommandList commandList) {
        initializeSimulation();
        List<StepStatus> stepStatuses = new ArrayList<>();
        
        for (Command command : commandList.getCommands()) {
            if (command instanceof AddVehicleCommand addVehicleCommand) {
                addVehicle(addVehicleCommand);
            } else if (command instanceof StepCommand) {
                StepStatus stepStatus = executeStep();
                stepStatuses.add(stepStatus);
            }
        }
        
        return new SimulationResult(stepStatuses);
    }
    
    public void addVehicle(AddVehicleCommand command) {
        String vehicleId = command.getVehicleId();
        String startRoadStr = command.getStartRoad().toUpperCase();
        String endRoadStr = command.getEndRoad().toUpperCase();
        
        try {
            Direction startRoad = Direction.valueOf(startRoadStr);
            Direction endRoad = Direction.valueOf(endRoadStr);
            
            Vehicle vehicle = new Vehicle(vehicleId, startRoad, endRoad);
            waitingVehicles.get(startRoad).add(vehicle);
        } catch (IllegalArgumentException e) {
            // Log error without throwing to prevent simulation from stopping
            System.err.println("Error adding vehicle: Invalid direction - " + e.getMessage());
        }
    }
    
    public StepStatus executeStep() {
        List<String> leftVehicles = new ArrayList<>();
        
        stepsInCurrentPhase++;
        
        System.out.println("\n--- Starting step " + stepsInCurrentPhase + " in phase " + currentPhase + " ---");
        for (Direction dir : Direction.values()) {
            TrafficLight light = trafficLights.get(dir);
            System.out.println(dir + " light: " + light.getMainState() + 
                              ", vehicles: " + waitingVehicles.get(dir).size());
            for (Vehicle v : waitingVehicles.get(dir)) {
                System.out.println("  " + v.getId() + " (" + v.getStartRoad() + " → " + v.getEndRoad() + 
                                  ", turn: " + v.getTurn() + ")");
            }
        }
        
        boolean isNorthSouthAxis = currentPhase == 0 || currentPhase == 2;
        Direction[] currentGreenAxis = isNorthSouthAxis ? 
            new Direction[]{Direction.NORTH, Direction.SOUTH} : 
            new Direction[]{Direction.EAST, Direction.WEST};
        
        boolean noVehiclesOnGreenAxis = true;
        for (Direction direction : currentGreenAxis) {
            if (!waitingVehicles.get(direction).isEmpty()) {
                noVehiclesOnGreenAxis = false;
                break;
            }
        }
        
        boolean vehiclesOnRedAxis = false;
        for (Direction direction : Direction.values()) {
            if (!java.util.Arrays.asList(currentGreenAxis).contains(direction) && 
                !waitingVehicles.get(direction).isEmpty()) {
                vehiclesOnRedAxis = true;
                break;
            }
        }
        
        boolean shouldSwitchPhase = false;
        
        if (noVehiclesOnGreenAxis && vehiclesOnRedAxis) {
            shouldSwitchPhase = true;
            System.out.println("Switching phase immediately because no vehicles on green axis and vehicles waiting on red axis");
        } 
        else if (stepsInCurrentPhase >= MIN_STEPS_PER_PHASE) {
            Direction[] redAxis = isNorthSouthAxis ? 
                new Direction[]{Direction.EAST, Direction.WEST} : 
                new Direction[]{Direction.NORTH, Direction.SOUTH};
            
            int greenAxisLoad = countVehiclesOnAxis(currentGreenAxis);
            int redAxisLoad = countVehiclesOnAxis(redAxis);
            
            System.out.println("Checking phase change conditions: green axis load = " + greenAxisLoad + 
                               ", red axis load = " + redAxisLoad + 
                               ", minimum steps required = " + MIN_STEPS_PER_PHASE + 
                               ", current steps = " + stepsInCurrentPhase);
            
            if (redAxisLoad > greenAxisLoad * 3) {
                shouldSwitchPhase = true;
                System.out.println("Switching phase due to heavy load on red axis: " + 
                                  redAxisLoad + " vs " + greenAxisLoad + " (ratio > 3)");
            } else {
                System.out.println("Not switching phase: red axis load (" + redAxisLoad + 
                                  ") is not > 3 times green axis load (" + greenAxisLoad + ")");
            }
        } else {
            System.out.println("Not switching phase: minimum steps not reached yet (" + 
                              stepsInCurrentPhase + "/" + MIN_STEPS_PER_PHASE + ")");
        }
        
        if (shouldSwitchPhase) {
            switchToNextPhase();
            stepsInCurrentPhase = 0;
            
            isNorthSouthAxis = currentPhase == 0 || currentPhase == 2;
            currentGreenAxis = isNorthSouthAxis ? 
                new Direction[]{Direction.NORTH, Direction.SOUTH} : 
                new Direction[]{Direction.EAST, Direction.WEST};
        }
        
        for (Direction direction : Direction.values()) {
            TrafficLight light = trafficLights.get(direction);
            boolean isGreen = java.util.Arrays.asList(currentGreenAxis).contains(direction);
            light.setMainState(isGreen ? TrafficLightState.GREEN : TrafficLightState.RED);
            
            if (rightTurnArrowsEnabled) {
                light.setRightTurnArrow(true);
            } else if (conditionalRightTurnArrowsEnabled && !isGreen) {
                Direction leftDirection = getLeftDirection(direction);
                boolean showConditionalArrow = !hasStraightVehicles(leftDirection);
                light.setRightTurnArrow(showConditionalArrow);
                
                if (showConditionalArrow) {
                    System.out.println("Enabling conditional right turn arrow for " + direction + 
                                      " because no straight vehicles from " + leftDirection);
                }
            } else {
                light.setRightTurnArrow(false);
            }
        }
        
        if (isNorthSouthAxis) {
            processAxisVehicles(Direction.NORTH, Direction.SOUTH, leftVehicles);
        } else {
            processAxisVehicles(Direction.EAST, Direction.WEST, leftVehicles);
        }
        
        if (rightTurnArrowsEnabled) {
            for (Direction direction : Direction.values()) {
                if (trafficLights.get(direction).getMainState() == TrafficLightState.RED) {
                    processRightTurningVehicles(direction, leftVehicles);
                }
            }
        }
        else if (conditionalRightTurnArrowsEnabled) {
            List<Direction> directionsWithConditionalArrow = new ArrayList<>();
            for (Direction direction : Direction.values()) {
                TrafficLight light = trafficLights.get(direction);
                if (light.getMainState() == TrafficLightState.RED && light.getRightTurnArrow()) {
                    directionsWithConditionalArrow.add(direction);
                }
            }
            
            for (Direction direction : directionsWithConditionalArrow) {
                processConditionalRightTurns(direction, leftVehicles);
            }
        }
        
        System.out.println("Step completed. Vehicles that left: " + leftVehicles);
        return new StepStatus(leftVehicles);
    }
    
    private int countVehiclesOnAxis(Direction[] axis) {
        int count = 0;
        for (Direction direction : axis) {
            count += waitingVehicles.get(direction).size();
        }
        return count;
    }
    

    private boolean hasStraightVehicles(Direction direction) {
        Queue<Vehicle> vehicles = waitingVehicles.get(direction);
        
        boolean hasStraight = false;
        StringBuilder vehiclesInfo = new StringBuilder();
        
        if (!vehicles.isEmpty()) {
            vehiclesInfo.append("Vehicles from ").append(direction).append(": ");
            
            for (Vehicle v : vehicles) {
                vehiclesInfo.append(v.getId())
                           .append(" (")
                           .append(v.getTurn())
                           .append("), ");
                
                if (v.getTurn() == Turn.STRAIGHT) {
                    hasStraight = true;
                }
            }
            
            System.out.println(vehiclesInfo.toString());
            System.out.println("Has straight-going vehicles: " + hasStraight);
        } else {
            System.out.println("No vehicles from " + direction);
        }
        
        return hasStraight;
    }
    
    private boolean hasStraightOrRightVehicles(Direction direction) {
        Queue<Vehicle> vehicles = waitingVehicles.get(direction);
        return vehicles.stream().anyMatch(v -> v.getTurn() == Turn.STRAIGHT || v.getTurn() == Turn.RIGHT);
    }
    
    private void processAxisVehicles(Direction direction1, Direction direction2, List<String> leftVehicles) {
        // Najpierw sprawdź, czy są pojazdy na obu kierunkach osi
        Queue<Vehicle> vehicles1 = waitingVehicles.get(direction1);
        Queue<Vehicle> vehicles2 = waitingVehicles.get(direction2);
        
        if (vehicles1.isEmpty() && vehicles2.isEmpty()) {
            return; 
        }
        
        boolean isGreen1 = trafficLights.get(direction1).getMainState() == TrafficLightState.GREEN;
        boolean isGreen2 = trafficLights.get(direction2).getMainState() == TrafficLightState.GREEN;
        
        if (!vehicles1.isEmpty() && !vehicles2.isEmpty() && isGreen1 && isGreen2) {
            Vehicle vehicle1 = vehicles1.peek();
            Vehicle vehicle2 = vehicles2.peek();
            
            boolean vehicle1TurnsLeft = vehicle1.getTurn() == Turn.LEFT;
            boolean vehicle2TurnsLeft = vehicle2.getTurn() == Turn.LEFT;
            
            // Sprawdź, czy  jadą do tego samego celu
            boolean sameDestination = vehicle1.getEndRoad() == vehicle2.getEndRoad();
            
            boolean collision = false;
            
            if (sameDestination && vehicle1.getStartRoad() != vehicle2.getStartRoad()) {
                collision = true;
                System.out.println("Collision detected: both vehicles want to go to " + 
                                  vehicle1.getEndRoad() + " from opposite directions");
                
                if (vehicle1.getTurn() == Turn.RIGHT) {
                    leftVehicles.add(vehicle1.getId());
                    vehicles1.poll();
                    
                    leftTurningVehiclesWaiting.put(vehicle2.getId(), 
                        leftTurningVehiclesWaiting.getOrDefault(vehicle2.getId(), 0) + 1);
                    
                    System.out.println("Vehicle " + vehicle1.getId() + " from " + direction1 + 
                                      " has priority (turning right) and leaves the intersection");
                } else if (vehicle2.getTurn() == Turn.RIGHT) {
                    leftVehicles.add(vehicle2.getId());
                    vehicles2.poll();
                    
                    leftTurningVehiclesWaiting.put(vehicle1.getId(), 
                        leftTurningVehiclesWaiting.getOrDefault(vehicle1.getId(), 0) + 1);
                    
                    System.out.println("Vehicle " + vehicle2.getId() + " from " + direction2 + 
                                      " has priority (turning right) and leaves the intersection");
                } else {
                    leftVehicles.add(vehicle1.getId());
                    vehicles1.poll();
                    System.out.println("Vehicle " + vehicle1.getId() + " from " + direction1 + 
                                      " passes first (arbitrary choice for same turn type)");
                }
            }
            else if (vehicle1TurnsLeft && vehicle2.getTurn() == Turn.STRAIGHT && 
                    conflictingPaths(vehicle1.getStartRoad(), vehicle1.getEndRoad(), vehicle2.getStartRoad(), vehicle2.getEndRoad())) {
                collision = true;
                leftVehicles.add(vehicle2.getId());
                vehicles2.poll();
                
                leftTurningVehiclesWaiting.put(vehicle1.getId(), 
                    leftTurningVehiclesWaiting.getOrDefault(vehicle1.getId(), 0) + 1);
                
                System.out.println("Vehicle " + vehicle2.getId() + " from " + direction2 + 
                                   " has priority (going straight) and leaves the intersection");
            } else if (vehicle2TurnsLeft && vehicle1.getTurn() == Turn.STRAIGHT && 
                      conflictingPaths(vehicle2.getStartRoad(), vehicle2.getEndRoad(), vehicle1.getStartRoad(), vehicle1.getEndRoad())) {
                collision = true;
                leftVehicles.add(vehicle1.getId());
                vehicles1.poll();
                
                leftTurningVehiclesWaiting.put(vehicle2.getId(), 
                    leftTurningVehiclesWaiting.getOrDefault(vehicle2.getId(), 0) + 1);
                
                System.out.println("Vehicle " + vehicle1.getId() + " from " + direction1 + 
                                   " has priority (going straight) and leaves the intersection");
            }
            
            if (!collision) {
                leftVehicles.add(vehicle1.getId());
                leftVehicles.add(vehicle2.getId());
                vehicles1.poll();
                vehicles2.poll();
                
                leftTurningVehiclesWaiting.remove(vehicle1.getId());
                leftTurningVehiclesWaiting.remove(vehicle2.getId());
                
                System.out.println("Both vehicles (" + vehicle1.getId() + " and " + vehicle2.getId() + 
                                   ") leave the intersection without conflict");
            }
        } else {
            processDirectionWithGreenLight(direction1, vehicles1, leftVehicles, isGreen1);
            processDirectionWithGreenLight(direction2, vehicles2, leftVehicles, isGreen2);
        }
    }
    
    private boolean conflictingPaths(Direction leftStart, Direction leftEnd, Direction straightStart, Direction straightEnd) {

        if (leftStart == Direction.NORTH && leftEnd == Direction.WEST && 
            straightStart == Direction.SOUTH && straightEnd == Direction.NORTH) {
            return true;
        }
        
        // SOUTH->EAST koliduje z NORTH->SOUTH
        if (leftStart == Direction.SOUTH && leftEnd == Direction.EAST && 
            straightStart == Direction.NORTH && straightEnd == Direction.SOUTH) {
            return true;
        }
        
        // EAST->NORTH koliduje z WEST->EAST
        if (leftStart == Direction.EAST && leftEnd == Direction.NORTH && 
            straightStart == Direction.WEST && straightEnd == Direction.EAST) {
            return true;
        }
        
        // WEST->SOUTH koliduje z EAST->WEST
        if (leftStart == Direction.WEST && leftEnd == Direction.SOUTH && 
            straightStart == Direction.EAST && straightEnd == Direction.WEST) {
            return true;
        }
        
        // WEST->NORTH koliduje z EAST->WEST
        if (leftStart == Direction.WEST && leftEnd == Direction.NORTH && 
            straightStart == Direction.EAST && straightEnd == Direction.WEST) {
            return true;
        }
        
        // EAST->SOUTH koliduje z WEST->EAST
        if (leftStart == Direction.EAST && leftEnd == Direction.SOUTH && 
            straightStart == Direction.WEST && straightEnd == Direction.EAST) {
            return true;
        }
        
        // NORTH->EAST koliduje z SOUTH->NORTH
        if (leftStart == Direction.NORTH && leftEnd == Direction.EAST && 
            straightStart == Direction.SOUTH && straightEnd == Direction.NORTH) {
            return true;
        }
        
        // SOUTH->WEST koliduje z NORTH->SOUTH
        if (leftStart == Direction.SOUTH && leftEnd == Direction.WEST && 
            straightStart == Direction.NORTH && straightEnd == Direction.SOUTH) {
            return true;
        }
        
        return false;
    }

    private void processDirectionWithGreenLight(Direction direction, Queue<Vehicle> vehicles, 
                                               List<String> leftVehicles, boolean isGreen) {
        if (vehicles.isEmpty()) {
            return;
        }
        
        Vehicle vehicle = vehicles.peek();
        
        if (!isGreen && vehicle.getTurn() == Turn.LEFT && 
            leftTurningVehiclesWaiting.getOrDefault(vehicle.getId(), 0) > 0) {
            
            leftVehicles.add(vehicle.getId());
            vehicles.poll();
            leftTurningVehiclesWaiting.remove(vehicle.getId());
            
            System.out.println("Vehicle " + vehicle.getId() + " from " + direction + 
                              " leaves despite red light (was already waiting to turn left)");
        }
        else if (isGreen) {
            leftVehicles.add(vehicle.getId());
            vehicles.poll();
            
            leftTurningVehiclesWaiting.remove(vehicle.getId());
            
            System.out.println("Vehicle " + vehicle.getId() + " from " + direction + 
                              " leaves the intersection");
        }
    }
    

    private void processRightTurningVehicles(Direction direction, List<String> leftVehicles) {
        Queue<Vehicle> vehicles = waitingVehicles.get(direction);
        
        if (vehicles.isEmpty()) {
            return;
        }
        
        Vehicle vehicle = vehicles.peek();
        
        if (vehicle.getTurn() == Turn.RIGHT) {
            vehicles.poll();
            leftVehicles.add(vehicle.getId());
            
            System.out.println("Vehicle " + vehicle.getId() + " from " + direction + 
                              " turns right on green arrow");
        } else {
            System.out.println("Vehicle " + vehicle.getId() + " from " + direction + 
                              " cannot use green right arrow (not turning right)");
        }
    }
    
    private void switchToNextPhase() {
        currentPhase = (currentPhase + 1) % phaseSequence.length;
        System.out.println("Switching to phase " + currentPhase + 
                          " (after " + stepsInCurrentPhase + " steps in previous phase)");
    }
    
    public void setRightTurnArrowsEnabled(boolean enabled) {
        this.rightTurnArrowsEnabled = enabled;
        
        // Update all traffic lights
        if (trafficLights != null) {
            for (TrafficLight light : trafficLights.values()) {
                light.setRightTurnArrow(enabled);
            }
        }
    }
    
    public void setConditionalRightTurnArrowsEnabled(boolean enabled) {
        this.conditionalRightTurnArrowsEnabled = enabled;
        
        // If enabling conditional arrows, disable normal right turn arrows
        if (enabled) {
            this.rightTurnArrowsEnabled = false;
        }
        
        // We'll update the traffic lights in the next step execution
    }
    
    public void setMaxVehiclesToProcess(int maxVehicles) {
        this.maxVehiclesToProcess = maxVehicles;
    }
    
    public Map<Direction, TrafficLight> getTrafficLightStates() {
        // Check if traffic lights are initialized
        if (trafficLights == null) {
            initializeSimulation();
        }
        return new HashMap<>(trafficLights);
    }
    
    public Map<Direction, Integer> getWaitingVehicleCounts() {
        if (waitingVehicles == null) {
            initializeSimulation();
        }
        return waitingVehicles.entrySet().stream()
            .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().size()));
    }

    private Direction getOpposingDirection(Direction direction) {
        return switch (direction) {
            case NORTH -> Direction.SOUTH;
            case SOUTH -> Direction.NORTH;
            case EAST -> Direction.WEST;
            case WEST -> Direction.EAST;
        };
    }

    private Direction getLeftDirection(Direction direction) {
        return switch (direction) {
            case NORTH -> Direction.WEST;
            case EAST -> Direction.NORTH;
            case SOUTH -> Direction.EAST;
            case WEST -> Direction.SOUTH;
        };
    }

    private void processConditionalRightTurns(Direction direction, List<String> leftVehicles) {
        Queue<Vehicle> vehicles = waitingVehicles.get(direction);
        
        if (vehicles.isEmpty()) {
            return;
        }
        
        System.out.println("Checking conditional right turns for " + direction);
        
        Vehicle vehicle = vehicles.peek();
        
        if (vehicle.getTurn() == Turn.RIGHT) {
            Direction leftDirection = getLeftDirection(direction);
            
            boolean hasStraightVehiclesFromLeft = hasStraightVehicles(leftDirection);
            
            System.out.println("  Vehicle " + vehicle.getId() + " wants to turn right. " +
                             "Straight vehicles from " + leftDirection + ": " + 
                             (hasStraightVehiclesFromLeft ? "YES (must wait)" : "NO (can proceed)"));
            
            if (!hasStraightVehiclesFromLeft) {
                vehicles.poll(); 
                leftVehicles.add(vehicle.getId());
                
                System.out.println("  Vehicle " + vehicle.getId() + " from " + direction + 
                                  " turns right on red (conditional right turn)");
            } else {
                System.out.println("  Vehicle " + vehicle.getId() + " from " + direction + 
                                  " cannot turn right on red (straight vehicles from left)");
            }
        } else {
            System.out.println("  Vehicle " + vehicle.getId() + " is not turning right, so conditional arrow doesn't apply");
        }
    }
} 