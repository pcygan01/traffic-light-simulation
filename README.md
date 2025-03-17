# Traffic Light Simulation

A realistic simulation of a four-way intersection with intelligent traffic light control and conditional turn management.

## Overview

This project simulates traffic flow at a standard intersection with four incoming roads. The simulation includes:
- Realistic traffic light phase management
- Conditional right turn on red functionality
- Special handling for left-turning vehicles
- Command line interface for batch processing
- Web interface for visual interaction

## Screenshots

Here are views of the application in action:

![Main Interface](https://github.com/user-attachments/assets/fdd8912c-c150-4321-baa5-b5ec9b4f0810)
*Control panel with simulation options*

![Vehicle Management](https://github.com/user-attachments/assets/2a3894d9-229e-4efb-bb36-5e8cce9d7fdf)
*Adding vehicles with route selection*

![Simulation Controls](https://github.com/user-attachments/assets/9f214175-8145-45d2-a294-bad081374412)
*Vehicle tracking interface*

![Results View](https://github.com/user-attachments/assets/69971246-0e66-4062-995f-81ed5be0fb98)
*Simulation steps and statistics*

![Step Details](https://github.com/user-attachments/assets/6634ef36-5fae-45ed-b790-afd9f3ad47cf)
*Vehicles leaving in each simulation step*

## Features

### Traffic Light Algorithm
- Dynamic phase changes based on traffic density on each axis
- Minimum phase duration to prevent rapid switching
- Special handling for left turns and potential collisions
- Conditional right turn arrows for smoother traffic flow

### Vehicle Processing
- Realistic movement patterns respecting traffic rules
- Vehicles can turn right on red when safe
- Left-turning vehicles yield to oncoming traffic
- Proper handling of vehicle queues from all directions

## Algorithm Details

The traffic light control algorithm makes intelligent decisions based on the current state of the intersection:

### Phase Change Conditions
- **Empty Road Priority**: If one axis has no waiting vehicles while vehicles are waiting on the other axis, lights change immediately
- **Minimum Phase Duration**: Lights will not change until at least 5 steps have occurred in the current phase (unless the active roads are empty)
- **Maximum Phase Duration**: Lights will change after a maximum of 50 steps in one phase
- **Traffic Imbalance**: Lights will change during operation when the number of vehicles waiting on the red axis is at least 3 times greater than those on the green axis

This approach ensures efficient traffic flow while preventing unnecessary switching that could cause delays. The algorithm prioritizes balanced waiting times across all directions while adapting to real-time traffic conditions.

## Running the Simulation

### Command Line Interface

Run the simulation from the command line to process JSON input files:

**Linux/macOS:**
```bash
./simulation.sh input.json output.json
```

**Windows:**
```bash
.\simulation.bat input.json output.json
```

Where:
- `input.json` - Path to input file with simulation commands
- `output.json` - Path where simulation results will be saved

### Web Interface

The application can also be run as a web service with a visual interface:

```bash
cd backend
mvn spring-boot:run
```

Then access the application at `http://localhost:8080`

### Local Development

To run the application locally for development:

1. **Install dependencies**:
   ```bash
   # In the frontend directory
   cd frontend
   npm install
   cd ..
   ```

2. **Start the application**:
   ```bash
   # Start the backend (Spring Boot)
   cd backend
   mvn spring-boot:run
   ```

   ```bash
   # In a separate terminal, start the frontend
   cd frontend
   npm start
   ```

   This will start:
   - The backend server on port 8080
   - The frontend development server on port 3000

3. **Access the application**:
   Open your browser and navigate to `http://localhost:3000`

## Input File Format

The input file should be in JSON format with a list of commands:

```json
{
  "commands": [
    {
      "type": "addVehicle",
      "vehicleId": "vehicle1",
      "startRoad": "NORTH",
      "endRoad": "SOUTH"
    },
    {
      "type": "step"
    },
    {
      "type": "addVehicle",
      "vehicleId": "vehicle2",
      "startRoad": "EAST",
      "endRoad": "WEST"
    },
    {
      "type": "step"
    }
  ]
}
```

## Output Format

The simulation produces a JSON output file with results for each step:

```json
{
  "stepStatuses": [
    {
      "leftVehicles": ["vehicle1"]
    },
    {
      "leftVehicles": ["vehicle2"]
    }
  ]
}
```

## Building the Project

To build the Java backend:

```bash
# Navigate to the backend directory
cd backend

# Build the project with Maven
mvn clean package
```

The executable JAR will be available in `backend/target/`.

### Running Tests

To run the unit tests:

```bash
# Navigate to the backend directory
cd backend

# Execute tests with Maven
mvn test
```

## Technologies

- **Backend**: Java, Spring Boot
- **Frontend**: React.js
- **Testing**: JUnit
- **Build Tool**: Maven

## Notes

- Traffic light changes are based on vehicle queue lengths and minimum phase durations
- Left-turning vehicles yield to straight-moving vehicles from the opposite direction
- Vehicles can make conditional right turns on red when safe
- The yellow light phase functions as a transition period where no vehicles move

## Author

Piotr Cygan 