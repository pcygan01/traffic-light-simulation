import React from 'react';
import './SimulationResults.css';

const SimulationResults = ({ results }) => {
  return (
    <div className="simulation-results">
      {results && results.stepStatuses && (
        <div className="results-section">
          <h3>Simulation Results</h3>
          <div className="step-results">
            {results.stepStatuses.map((step, index) => (
              <div key={index} className="step-result">
                <h4>Step {index + 1}</h4>
                <div className="vehicles-left">
                  <strong>Vehicles that left:</strong>
                  {step.leftVehicles.length === 0 ? (
                    <span className="no-vehicles">None</span>
                  ) : (
                    <ul>
                      {step.leftVehicles.map(vehicleId => (
                        <li key={vehicleId}>{vehicleId}</li>
                      ))}
                    </ul>
                  )}
                </div>
              </div>
            ))}
          </div>
        </div>
      )}
    </div>
  );
};

export default SimulationResults; 