import React, { useState, useEffect, useRef } from 'react';
import './SimulationVisualizer.css';

const SimulationVisualizer = ({ results, onStepChange }) => {
  const [currentStep, setCurrentStep] = useState(0);
  const [isAutoPlaying, setIsAutoPlaying] = useState(false);
  const autoPlayTimerRef = useRef(null);

  //Zostawiam ten plik, ponieważ bardzo długo próbowałem stworzyć ładną wizualizację, ale niestety nie udało mi się jej dokończyć na czas. 
  //Na pewno do tego wrócę i ulepsze wizualizację.
  
  useEffect(() => {
    console.log("SimulationVisualizer received results:", results);
    if (results && results.stepStatuses) {
      console.log(`Found ${results.stepStatuses.length} steps in simulation results`);
      console.log("First step has leftVehicles:", results.stepStatuses[0]?.leftVehicles);
      setCurrentStep(0);
      setIsAutoPlaying(false);
    }
  }, [results]);
  
  useEffect(() => {
    if (onStepChange) {
      console.log(`SimulationVisualizer: changing to step ${currentStep}`);
      
      onStepChange(currentStep);
    }
  }, [currentStep, onStepChange]);

  useEffect(() => {
    if (isAutoPlaying && results && results.stepStatuses) {
      console.log(`Auto-play started, current step: ${currentStep}/${results.stepStatuses.length - 1}`);
      
      if (autoPlayTimerRef.current) {
        clearInterval(autoPlayTimerRef.current);
      }
      
      autoPlayTimerRef.current = setInterval(() => {
        setCurrentStep(prevStep => {
          const nextStep = prevStep + 1;
          if (nextStep < results.stepStatuses.length) {
            console.log(`Auto-play: moving to step ${nextStep}`);
            return nextStep;
          } else {
            console.log(`Auto-play: reached end of simulation`);
            setIsAutoPlaying(false);
            return prevStep;
          }
        });
      }, 2000);
    } else if (autoPlayTimerRef.current) {
      console.log(`Auto-play stopped`);
      clearInterval(autoPlayTimerRef.current);
      autoPlayTimerRef.current = null;
    }
    
    return () => {
      if (autoPlayTimerRef.current) {
        clearInterval(autoPlayTimerRef.current);
        autoPlayTimerRef.current = null;
      }
    };
  }, [isAutoPlaying, results, currentStep]);

  if (!results || !results.stepStatuses || results.stepStatuses.length === 0) {
    console.log("No valid results found:", results);
    return (
      <div className="simulation-visualizer simulation-empty">
        <h3>Simulation Results</h3>
        <p>No simulation results available. Please upload a JSON file and run the simulation.</p>
      </div>
    );
  }

  const totalSteps = results.stepStatuses.length;
  const step = results.stepStatuses[currentStep];

  const nextStep = () => {
    if (currentStep < totalSteps - 1) {
      setCurrentStep(currentStep + 1);
    }
  };

  const prevStep = () => {
    if (currentStep > 0) {
      setCurrentStep(currentStep - 1);
    }
  };
  
  const jumpToStart = () => {
    setCurrentStep(0);
  };
  
  const jumpToEnd = () => {
    setCurrentStep(totalSteps - 1);
  };
  
  const toggleAutoPlay = () => {
    setIsAutoPlaying(!isAutoPlaying);
  };

  const totalVehiclesLeft = results.stepStatuses.reduce((total, step) => 
    total + step.leftVehicles.length, 0);
    
  const vehiclesLeftSoFar = results.stepStatuses.slice(0, currentStep + 1).reduce((total, step) => 
    total + step.leftVehicles.length, 0);

  return (
    <div className="simulation-visualizer">
      <h3>Simulation Results</h3>
      <div className="simulation-summary">
        <div className="summary-item">
          <span className="summary-label">Total Steps:</span>
          <span className="summary-value">{totalSteps}</span>
        </div>
        <div className="summary-item">
          <span className="summary-label">Total Vehicles Processed:</span>
          <span className="summary-value">{totalVehiclesLeft}</span>
        </div>
        <div className="summary-item">
          <span className="summary-label">Current Step:</span>
          <span className="summary-value">{currentStep + 1}</span>
        </div>
        <div className="summary-item">
          <span className="summary-label">Vehicles Processed So Far:</span>
          <span className="summary-value">{vehiclesLeftSoFar}</span>
        </div>
      </div>
      
      <div className="step-info">
        <h4>Step {currentStep + 1} of {totalSteps}</h4>
        <div className="vehicles-left">
          <strong>Vehicles that left in this step:</strong>
          {step.leftVehicles.length === 0 ? (
            <span className="no-vehicles">None</span>
          ) : (
            <div className="vehicle-list">
              {step.leftVehicles.map(vehicleId => (
                <div key={vehicleId} className="visualizer-vehicle">{vehicleId}</div>
              ))}
            </div>
          )}
        </div>
      </div>
      
      <div className="visualizer-controls">
        <button 
          onClick={jumpToStart} 
          disabled={currentStep === 0 || isAutoPlaying}
          className="control-button"
        >
          &lt;&lt; First
        </button>
        <button 
          onClick={prevStep} 
          disabled={currentStep === 0 || isAutoPlaying}
          className="control-button"
        >
          &lt; Previous
        </button>
        
        <button
          onClick={toggleAutoPlay}
          className={`control-button ${isAutoPlaying ? 'active-button' : ''}`}
        >
          {isAutoPlaying ? 'Stop' : 'Auto Play'}
        </button>
        
        <span className="step-counter">{currentStep + 1} / {totalSteps}</span>
        <button 
          onClick={nextStep} 
          disabled={currentStep === totalSteps - 1 || isAutoPlaying}
          className="control-button"
        >
          Next &gt;
        </button>
        <button 
          onClick={jumpToEnd} 
          disabled={currentStep === totalSteps - 1 || isAutoPlaying}
          className="control-button"
        >
          Last &gt;&gt;
        </button>
      </div>
    </div>
  );
};

export default SimulationVisualizer; 