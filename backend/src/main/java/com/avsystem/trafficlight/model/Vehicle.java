package com.avsystem.trafficlight.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a vehicle in the traffic simulation.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Vehicle {
    private String id;
    private Direction startRoad;
    private Direction endRoad;
    private Turn turn;

    public Vehicle(String id, Direction startRoad, Direction endRoad) {
        this.id = id;
        this.startRoad = startRoad;
        this.endRoad = endRoad;
        try {
            this.turn = Turn.fromDirections(startRoad, endRoad);
            System.out.println("Created vehicle " + id + " from " + startRoad + 
                               " to " + endRoad + " with turn direction: " + this.turn);
        } catch (Exception e) {
            System.err.println("Error calculating turn for vehicle " + id + 
                               " from " + startRoad + " to " + endRoad + ": " + e.getMessage());
            this.turn = Turn.STRAIGHT;
        }
    }
} 