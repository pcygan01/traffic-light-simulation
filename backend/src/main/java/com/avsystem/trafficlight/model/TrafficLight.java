package com.avsystem.trafficlight.model;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Represents a traffic light at an intersection approach.
 */
@Data
@AllArgsConstructor
public class TrafficLight {
    private Direction direction;
    private TrafficLightState mainState;
    private boolean rightTurnArrow;
    

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }
    
    public TrafficLightState getMainState() {
        return mainState;
    }
    
    public void setMainState(TrafficLightState mainState) {
        this.mainState = mainState;
    }
    
    public boolean getRightTurnArrow() {
        return rightTurnArrow;
    }

    public void setRightTurnArrow(boolean rightTurnArrow) {
        this.rightTurnArrow = rightTurnArrow;
    }
    
    /**
     * Checks if a vehicle can proceed through the intersection based on the traffic light state.
     * 
     * @param vehicle The vehicle to check
     * @param opposingTraffic Whether there is opposing traffic (relevant for left turns)
     * @return true if the vehicle can proceed, false otherwise
     */
    public boolean canVehicleProceed(Vehicle vehicle, boolean opposingTraffic) {
        if (vehicle.getStartRoad() != direction) {
            System.out.println("Vehicle " + vehicle.getId() + " is not at this traffic light (vehicle at " + 
                vehicle.getStartRoad() + ", light at " + direction + ")");
            return false;
        }
        
        Turn turn = vehicle.getTurn();
        System.out.println("Traffic light at " + direction + " is " + mainState + 
                           ", vehicle " + vehicle.getId() + " wants to turn " + turn + 
                           ", opposing traffic: " + opposingTraffic);
        
        if (turn == Turn.RIGHT && rightTurnArrow) {
            System.out.println("Vehicle " + vehicle.getId() + " can turn right on green arrow");
            return true;
        }
        
        if (mainState == TrafficLightState.GREEN) {
            if (turn == Turn.STRAIGHT) {
                System.out.println("Vehicle " + vehicle.getId() + " can go straight on green");
                return true;
            }
            
            if (turn == Turn.RIGHT) {
                System.out.println("Vehicle " + vehicle.getId() + " can turn right on green");
                return true;
            }
            
            if (turn == Turn.LEFT) {
                if (!opposingTraffic) {
                    System.out.println("Vehicle " + vehicle.getId() + " can turn left with no opposing traffic");
                    return true;
                } else {
                    System.out.println("Vehicle " + vehicle.getId() + " cannot turn left due to opposing traffic");
                    return false;
                }
            }
        }
        
        System.out.println("Vehicle " + vehicle.getId() + " cannot proceed (light is not green or invalid turn)");
        return false;
    }
} 