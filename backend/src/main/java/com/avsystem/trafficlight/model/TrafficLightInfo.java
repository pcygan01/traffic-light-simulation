package com.avsystem.trafficlight.model;

/**
 * Information about traffic light state for frontend
 */
public class TrafficLightInfo {
    private TrafficLightState mainState;
    private boolean rightTurnArrow;

    public TrafficLightInfo(TrafficLightState mainState, boolean rightTurnArrow) {
        this.mainState = mainState;
        this.rightTurnArrow = rightTurnArrow;
    }

    public TrafficLightState getMainState() {
        return mainState;
    }

    public void setMainState(TrafficLightState mainState) {
        this.mainState = mainState;
    }

    public boolean isRightTurnArrow() {
        return rightTurnArrow;
    }

    public void setRightTurnArrow(boolean rightTurnArrow) {
        this.rightTurnArrow = rightTurnArrow;
    }
} 