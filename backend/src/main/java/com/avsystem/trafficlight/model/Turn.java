package com.avsystem.trafficlight.model;

/**
 * Enum representing the possible turn directions for a vehicle.
 */
public enum Turn {
    LEFT, RIGHT, STRAIGHT;

    /**
     * Determines the exit direction based on entry direction and turn.
     * 
     * @param entryDirection The direction from which the vehicle enters the intersection
     * @return The direction in which the vehicle will exit the intersection
     */
    public Direction getExitDirection(Direction entryDirection) {
        return switch (entryDirection) {
            case NORTH -> switch (this) {
                case LEFT -> Direction.EAST;
                case RIGHT -> Direction.WEST;
                case STRAIGHT -> Direction.SOUTH;
            };
            case SOUTH -> switch (this) {
                case LEFT -> Direction.WEST;
                case RIGHT -> Direction.EAST;
                case STRAIGHT -> Direction.NORTH;
            };
            case EAST -> switch (this) {
                case RIGHT -> Direction.NORTH;
                case LEFT -> Direction.SOUTH;
                case STRAIGHT -> Direction.WEST;
            };
            case WEST -> switch (this) {
                case RIGHT -> Direction.SOUTH;
                case LEFT -> Direction.NORTH;
                case STRAIGHT -> Direction.EAST;
            };
        };
    }

    /**
     * Determines the turn direction based on entry and exit directions.
     * 
     * @param entryDirection The direction from which the vehicle enters the intersection
     * @param exitDirection The direction in which the vehicle will exit the intersection
     * @return The turn direction the vehicle will take
     */
    public static Turn fromDirections(Direction entryDirection, Direction exitDirection) {
        System.out.println("Calculating turn from " + entryDirection + " to " + exitDirection);
        Turn result;
        try {
            result = switch (entryDirection) {
                case NORTH -> switch (exitDirection) {
                    case EAST -> LEFT;
                    case WEST -> RIGHT;
                    case SOUTH -> STRAIGHT;
                    default -> throw new IllegalArgumentException("Invalid exit direction");
                };
                case SOUTH -> switch (exitDirection) {
                    case WEST -> LEFT;
                    case EAST -> RIGHT;
                    case NORTH -> STRAIGHT;
                    default -> throw new IllegalArgumentException("Invalid exit direction");
                };
                case EAST -> switch (exitDirection) {
                    case NORTH -> RIGHT;
                    case SOUTH -> LEFT;
                    case WEST -> STRAIGHT;
                    default -> throw new IllegalArgumentException("Invalid exit direction");
                };
                case WEST -> switch (exitDirection) {
                    case NORTH -> LEFT;
                    case SOUTH -> RIGHT;
                    case EAST -> STRAIGHT;
                    default -> throw new IllegalArgumentException("Invalid exit direction");
                };
            };
            System.out.println("Turn from " + entryDirection + " to " + exitDirection + " is " + result);
            return result;
        } catch (Exception e) {
            System.err.println("Error in fromDirections: " + e.getMessage());
            throw e;
        }
    }
} 