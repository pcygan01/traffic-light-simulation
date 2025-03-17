import React, { useState, useEffect } from 'react';
import './App.css';
import Controls from './components/Controls';
import SimulationResults from './components/SimulationResults';
import SimulationVisualizer from './components/SimulationVisualizer';
import JsonDisplay from './components/JsonDisplay';
import SimulationService from './services/SimulationService';

function App() {
  // eslint-disable-next-line no-unused-vars
  const [trafficLights, setTrafficLights] = useState({
    NORTH: { direction: 'NORTH', mainState: 'GREEN', rightTurnArrow: false },
    SOUTH: { direction: 'SOUTH', mainState: 'GREEN', rightTurnArrow: false },
    EAST: { direction: 'EAST', mainState: 'RED', rightTurnArrow: false },
    WEST: { direction: 'WEST', mainState: 'RED', rightTurnArrow: false }
  });
  
  // eslint-disable-next-line no-unused-vars
  const [vehicles, setVehicles] = useState([]);
  // eslint-disable-next-line no-unused-vars
  const [waitingVehicleCounts, setWaitingVehicleCounts] = useState({
    NORTH: 0,
    SOUTH: 0,
    EAST: 0,
    WEST: 0
  });
  
  const [simulationResults, setSimulationResults] = useState(null);
  const [isSimulationInitialized, setIsSimulationInitialized] = useState(false);
  const [isLoading, setIsLoading] = useState(false);
  // eslint-disable-next-line no-unused-vars
  const [currentSimulationStep, setCurrentSimulationStep] = useState(0);
  const [conditionalArrowsEnabled, setConditionalArrowsEnabled] = useState(false);
  
  useEffect(() => {
    initializeSimulation();
    
    const interval = setInterval(() => {
      if (isSimulationInitialized) {
        updateSimulationState();
      }
    }, 1000);
    
    return () => clearInterval(interval);
  // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [isSimulationInitialized]); // initializeSimulation i updateSimulationState są funkcjami definiowanymi wewnątrz komponentu,
                                 // więc nie muszą być dodawane jako zależności
  
  const updateSimulationState = async () => {
    try {
      const [lightsResponse, vehiclesResponse] = await Promise.all([
        SimulationService.getTrafficLightStates(),
        SimulationService.getWaitingVehicleCounts()
      ]);
      
      let updatedLights = lightsResponse.data;
      setTrafficLights(updatedLights);
      setWaitingVehicleCounts(vehiclesResponse.data);
      
      const vehicleObjects = [];
      Object.entries(vehiclesResponse.data).forEach(([direction, count]) => {
        for (let i = 0; i < count; i++) {
          vehicleObjects.push({
            id: `${direction}-${i}`,
            startRoad: direction,
            endRoad: getRandomEndRoad(direction),
            position: i
          });
        }
      });
      
      setVehicles(vehicleObjects);
    } catch (error) {
      console.error('Error updating simulation state:', error);
    }
  };
  
  const getRandomEndRoad = (startRoad) => {
    const directions = ['NORTH', 'SOUTH', 'EAST', 'WEST'];
    const availableDirections = directions.filter(dir => dir !== startRoad);
    const randomIndex = Math.floor(Math.random() * availableDirections.length);
    return availableDirections[randomIndex];
  };
  
  const initializeSimulation = async () => {
    try {
      await SimulationService.initializeSimulation();
      setIsSimulationInitialized(true);
      setSimulationResults(null);
      await updateSimulationState();
    } catch (error) {
      console.error('Error initializing simulation:', error);
    }
  };
  
  const handleAddVehicle = async (vehicleId, startRoad, endRoad) => {
    if (!isSimulationInitialized) {
      await initializeSimulation();
    }
    
    try {
      await SimulationService.addVehicle(vehicleId, startRoad, endRoad);
      await updateSimulationState();
    } catch (error) {
      console.error('Error adding vehicle:', error);
    }
  };
  
  const handleStep = async () => {
    if (!isSimulationInitialized) {
      await initializeSimulation();
    }
    
    try {
      const response = await SimulationService.executeStep();
      await updateSimulationState();
      
      if (simulationResults) {
        setSimulationResults({
          stepStatuses: [...simulationResults.stepStatuses, response.data]
        });
      } else {
        setSimulationResults({
          stepStatuses: [response.data]
        });
      }
    } catch (error) {
      console.error('Error executing step:', error);
    }
  };
  
  const handleToggleConditionalArrows = async (enabled) => {
    setConditionalArrowsEnabled(enabled);
    
    try {
      await SimulationService.setConditionalRightTurnArrowsEnabled(enabled);
      console.log(`Conditional arrows ${enabled ? 'enabled' : 'disabled'}`);
      
      await updateSimulationState();
    } catch (error) {
      console.error('Error toggling conditional arrows:', error);
    }
  };
  
  const determineTurnDirection = (startRoad, endRoad) => {
    const start = startRoad.toUpperCase();
    const end = endRoad.toUpperCase();
    
    
    if (
      (start === 'NORTH' && end === 'SOUTH') ||
      (start === 'SOUTH' && end === 'NORTH') ||
      (start === 'EAST' && end === 'WEST') ||
      (start === 'WEST' && end === 'EAST')
    ) {
      return 'STRAIGHT';
    }
    
    if (
      (start === 'NORTH' && end === 'EAST') ||
      (start === 'EAST' && end === 'SOUTH') ||
      (start === 'SOUTH' && end === 'WEST') ||
      (start === 'WEST' && end === 'NORTH')
    ) {
      return 'LEFT';
    }
    
    if (
      (start === 'NORTH' && end === 'WEST') ||
      (start === 'WEST' && end === 'SOUTH') ||
      (start === 'SOUTH' && end === 'EAST') ||
      (start === 'EAST' && end === 'NORTH')
    ) {
      return 'RIGHT';
    }
    
    console.warn(`Unrecognized direction combination: ${start} -> ${end}, assuming straight movement`);
    return 'STRAIGHT';
  };
  
  // eslint-disable-next-line no-unused-vars
  const getRealTurnDirection = (startRoad, endRoad) => {
    const start = startRoad.toUpperCase();
    const end = endRoad.toUpperCase();
    
    if (
      (start === 'NORTH' && end === 'EAST') ||
      (start === 'EAST' && end === 'SOUTH') ||
      (start === 'SOUTH' && end === 'WEST') ||
      (start === 'WEST' && end === 'NORTH')
    ) {
      return 'LEWO';
    } else if (
      (start === 'NORTH' && end === 'WEST') ||
      (start === 'WEST' && end === 'SOUTH') ||
      (start === 'SOUTH' && end === 'EAST') ||
      (start === 'EAST' && end === 'NORTH')
    ) {
      return 'PRAWO';
    } else {
      return 'PROSTO';
    }
  };
  
  const updateIntersectionForStep = (stepIndex) => {
    if (!simulationResults || !simulationResults.stepStatuses || stepIndex >= simulationResults.stepStatuses.length) {
      console.log("No simulation data or invalid step index");
      return;
    }
    
    setCurrentSimulationStep(stepIndex);
    console.log(`Updating visualization for step ${stepIndex+1}/${simulationResults.stepStatuses.length}`);
    
    const vehicleDetails = {};
    if (simulationResults.rawJson) {
      try {
        const jsonData = JSON.parse(simulationResults.rawJson);
        if (jsonData && jsonData.commands) {
          let stepCounter = 0;
          jsonData.commands.forEach(command => {
            if (command.type === 'addVehicle' && command.vehicleId && command.startRoad && command.endRoad) {
              vehicleDetails[command.vehicleId] = {
                startRoad: command.startRoad.toUpperCase(),
                endRoad: command.endRoad.toUpperCase(),
                turnDirection: determineTurnDirection(command.startRoad, command.endRoad),
                addedBeforeStep: stepCounter
              };
            } else if (command.type === 'step') {
              stepCounter++;
            }
          });
        }
      } catch (error) {
        console.error("Error parsing JSON data:", error);
      }
    }
    
    const exitedVehicles = new Set();
    for (let i = 0; i <= stepIndex; i++) {
      (simulationResults.stepStatuses[i].leftVehicles || []).forEach(vehicleId => {
        exitedVehicles.add(vehicleId);
      });
    }
    
    const vehicleCountsByDirection = { NORTH: 0, SOUTH: 0, EAST: 0, WEST: 0 };
    
    Object.keys(vehicleDetails).forEach(vehicleId => {
      if (vehicleDetails[vehicleId].addedBeforeStep <= stepIndex && !exitedVehicles.has(vehicleId)) {
        const direction = vehicleDetails[vehicleId].startRoad;
        vehicleCountsByDirection[direction]++;
      }
    });
    
    console.log("Simulation step:", stepIndex + 1);
    console.log("Vehicles that left the intersection up to this step:", Array.from(exitedVehicles));
    console.log("Remaining vehicles at the intersection:", vehicleCountsByDirection);
    
    setWaitingVehicleCounts(vehicleCountsByDirection);
    
    const isNorthSouthGreen = Math.floor(stepIndex / 5) % 2 === 0;
    
    let updatedLights = {
      NORTH: { direction: 'NORTH', mainState: isNorthSouthGreen ? 'GREEN' : 'RED', rightTurnArrow: false },
      SOUTH: { direction: 'SOUTH', mainState: isNorthSouthGreen ? 'GREEN' : 'RED', rightTurnArrow: false },
      EAST: { direction: 'EAST', mainState: isNorthSouthGreen ? 'RED' : 'GREEN', rightTurnArrow: false },
      WEST: { direction: 'WEST', mainState: isNorthSouthGreen ? 'RED' : 'GREEN', rightTurnArrow: false }
    };
    
    if (conditionalArrowsEnabled) {
      const simulationVehicles = [];
      Object.keys(vehicleDetails).forEach(vehicleId => {
        if (vehicleDetails[vehicleId].addedBeforeStep <= stepIndex && !exitedVehicles.has(vehicleId)) {
          simulationVehicles.push({
            id: vehicleId,
            startRoad: vehicleDetails[vehicleId].startRoad,
            endRoad: vehicleDetails[vehicleId].endRoad
          });
        }
      });
      
      Object.keys(updatedLights).forEach(direction => {
        if (updatedLights[direction].mainState === 'RED') {
          const leftDirection = getLeftDirection(direction);
          
          const vehiclesFromLeft = simulationVehicles.filter(v => v.startRoad === leftDirection);
          
          const anyStraight = vehiclesFromLeft.some(v => {
            const turnDirection = determineTurnDirection(v.startRoad, v.endRoad);
            return turnDirection === 'STRAIGHT';
          });
          
          updatedLights[direction].rightTurnArrow = !anyStraight;
          
          console.log(`${direction}: Conditional right turn arrow ${!anyStraight ? 'ACTIVE' : 'inactive'} (vehicles from the left going straight: ${anyStraight ? 'YES' : 'NO'})`);
        }
      });
    }
    
    setTrafficLights(updatedLights);
  };
  
  const getLeftDirection = (direction) => {
    switch(direction) {
      case 'NORTH': return 'WEST';
      case 'EAST': return 'NORTH';
      case 'SOUTH': return 'EAST';
      case 'WEST': return 'SOUTH';
      default: return direction;
    }
  };
  
  // eslint-disable-next-line no-unused-vars
  const getOpposingDirection = (direction) => {
    switch(direction) {
      case 'NORTH': return 'SOUTH';
      case 'SOUTH': return 'NORTH';
      case 'EAST': return 'WEST';
      case 'WEST': return 'EAST';
      default: return direction;
    }
  };
  
  const handleFileUpload = async (commandList) => {
    try {
      setIsLoading(true);
      console.log("Running simulation with commands:", commandList);
      
      console.log(`Setting conditional right turn arrows to ${conditionalArrowsEnabled}`);
      await SimulationService.setConditionalRightTurnArrowsEnabled(conditionalArrowsEnabled);
      
      const response = await SimulationService.runSimulation(commandList);
      console.log("Simulation response:", response);
      
      await new Promise(resolve => setTimeout(resolve, 2000));
      
      if (response.data && response.data.stepStatuses) {
        console.log("Setting simulation results with step statuses:", response.data.stepStatuses);
        
        const processedResults = {
          stepStatuses: response.data.stepStatuses.map(step => {
            if (!step.leftVehicles) {
              step.leftVehicles = [];
            }
            return step;
          }),
          rawJson: JSON.stringify(response.data, null, 2)
        };
        
        console.log("Processed results:", processedResults);
        setSimulationResults(processedResults);
        
        setCurrentSimulationStep(0);
        updateIntersectionForStep(0);
        
        setTimeout(() => {
          startSimulationAnimation(processedResults);
        }, 1000);
      } else {
        console.error("Invalid simulation results:", response.data);
      }
    } catch (error) {
      console.error('Error running simulation from file:', error);
      console.error('Error details:', error.response ? error.response.data : 'No response data');
    } finally {
      setIsLoading(false);
    }
  };
  
  const startSimulationAnimation = (results) => {
    if (!results || !results.stepStatuses || results.stepStatuses.length === 0) {
      return;
    }
    
    let step = 0;
    updateIntersectionForStep(step);
    
    const animationInterval = setInterval(() => {
      step++;
      
      if (step >= results.stepStatuses.length) {
        clearInterval(animationInterval);
        return;
      }
      
      updateIntersectionForStep(step);
    }, 2000); 
    
    window.simulationAnimationInterval = animationInterval;
    
    return () => {
      if (window.simulationAnimationInterval) {
        clearInterval(window.simulationAnimationInterval);
      }
    };
  };
  
  const handleVisualizerStepChange = (stepIndex) => {
    updateIntersectionForStep(stepIndex);
  };
  
  const renderSimulationComponents = () => {
    if (!simulationResults) return null;
    
    return (
      <>
        <JsonDisplay jsonData={simulationResults.rawJson} />
        
        <SimulationVisualizer 
          results={simulationResults} 
          onStepChange={handleVisualizerStepChange} 
        />
        
        <SimulationResults 
          results={simulationResults}
        />
      </>
    );
  };
  
  return (
    <div className="app">
      <header className="app-header">
        <h1>Traffic Light Simulation</h1>
      </header>
      
      <main className="app-main">
        <Controls 
          onAddVehicle={handleAddVehicle}
          onStep={handleStep}
          onInitialize={initializeSimulation}
          onFileUpload={handleFileUpload}
          onToggleConditionalArrows={handleToggleConditionalArrows}
          conditionalArrowsEnabled={conditionalArrowsEnabled}
        />
        
        {isLoading && (
          <div className="loading-indicator">
            <p>Running simulation... Please wait.</p>
          </div>
        )}
        
        {}
        
        {renderSimulationComponents()}
      </main>
      
      <footer className="app-footer">
        <p>Intelligent Traffic Light Simulation &copy; 2025</p>
      </footer>
    </div>
  );
}

export default App; 