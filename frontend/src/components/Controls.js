import React, { useState, useRef } from 'react';
import './Controls.css';

function Controls({ 
  onAddVehicle, 
  onStep, 
  onInitialize, 
  onFileUpload,
  conditionalArrowsEnabled,
  onToggleConditionalArrows
}) {
  const [vehicleId, setVehicleId] = useState('');
  const [startRoad, setStartRoad] = useState('NORTH');
  const [endRoad, setEndRoad] = useState('SOUTH');
  const [file, setFile] = useState(null);
  const [addVehicleError, setAddVehicleError] = useState('');
  const [fileError, setFileError] = useState('');
  const [addedVehicles, setAddedVehicles] = useState([]);
  const fileInputRef = useRef(null);

  const handleAddVehicle = () => {
    if (!vehicleId) {
      setAddVehicleError('Please enter a vehicle ID');
      return;
    }
    
    setAddVehicleError('');
    onAddVehicle(vehicleId, startRoad, endRoad);
    
    const newVehicle = {
      id: vehicleId,
      startRoad,
      endRoad,
      turnDirection: determineTurnDirection(startRoad, endRoad),
      addedAt: new Date().toLocaleTimeString()
    };
    setAddedVehicles(prev => [newVehicle, ...prev]);
    
    setVehicleId('');
  };

  const handleInitialize = () => {
    setAddedVehicles([]);
    onInitialize();
  };

  const handleFileChange = (e) => {
    const file = e.target.files[0];
    setFile(file);
    setFileError('');
    
    if (file) {
      const reader = new FileReader();
      reader.onload = (e) => {
        try {
          const json = JSON.parse(e.target.result);
          console.log("Read JSON from file:", json);
          setFileError('');
        } catch (error) {
          console.error("Error parsing JSON:", error);
          setFileError('Invalid JSON file. Please check the file format.');
        }
      };
      reader.readAsText(file);
    }
  };

  const handleFileUpload = () => {
    if (!file) {
      setFileError('Please select a file');
      return;
    }

    const reader = new FileReader();
    reader.onload = (e) => {
      try {
        const json = JSON.parse(e.target.result);
        setFileError('');
        console.log("Submitting JSON:", json);
        setAddedVehicles([]);
        onFileUpload(json);
      } catch (error) {
        console.error("Error parsing JSON:", error);
        setFileError('Invalid JSON file. Please check the file format.');
      }
    };
    reader.readAsText(file);
    
    if (fileInputRef.current) {
      fileInputRef.current.value = '';
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
    
    return 'RIGHT';
  };

  return (
    <div className="controls">
      <div className="control-section">
        <h3>Interactive Simulation</h3>
        <button onClick={handleInitialize}>Initialize Simulation</button>
        <button onClick={onStep}>Execute Step</button>
        
        <div className="control-group conditional-arrows">
          <label>
            <input 
              type="checkbox" 
              checked={conditionalArrowsEnabled} 
              onChange={(e) => onToggleConditionalArrows(e.target.checked)} 
            />
            Enable conditional right turn
          </label>
          <div className="tooltip">
            <span className="tooltip-text">
              Allows right turn on red light when vehicles from the left are turning (not going straight)
            </span>
            <span className="info-icon">?</span>
          </div>
        </div>
      </div>
      
      <div className="control-section">
        <h3>Add Vehicle</h3>
        <div className="control-group">
          <label>Vehicle ID:</label>
          <input 
            type="text" 
            value={vehicleId} 
            onChange={(e) => setVehicleId(e.target.value)} 
            placeholder="e.g. car1"
          />
        </div>
        
        {addVehicleError && <div className="error-message">{addVehicleError}</div>}
        
        <div className="control-group">
          <label>Start Road:</label>
          <select value={startRoad} onChange={(e) => setStartRoad(e.target.value)}>
            <option value="NORTH">NORTH</option>
            <option value="SOUTH">SOUTH</option>
            <option value="EAST">EAST</option>
            <option value="WEST">WEST</option>
          </select>
        </div>
        
        <div className="control-group">
          <label>End Road:</label>
          <select value={endRoad} onChange={(e) => setEndRoad(e.target.value)}>
            <option value="NORTH">NORTH</option>
            <option value="SOUTH">SOUTH</option>
            <option value="EAST">EAST</option>
            <option value="WEST">WEST</option>
          </select>
        </div>
        
        <button onClick={handleAddVehicle}>Add Vehicle</button>
      </div>
      
      {}
      {addedVehicles.length > 0 && (
        <div className="control-section">
          <h3>Added Vehicles ({addedVehicles.length})</h3>
          <div className="added-vehicles-list">
            {addedVehicles.map((vehicle, index) => (
              <div key={index} className="added-vehicle-item">
                <div className="vehicle-id">{vehicle.id}</div>
                <div className="vehicle-route">
                  {vehicle.startRoad} → {vehicle.endRoad} ({vehicle.turnDirection})
                </div>
                <div className="vehicle-time">{vehicle.addedAt}</div>
              </div>
            ))}
          </div>
        </div>
      )}
      
      <div className="control-section">
        <h3>JSON Simulation</h3>
        <div className="control-group">
          <label>Upload JSON File:</label>
          <input type="file" accept=".json" onChange={handleFileChange} ref={fileInputRef} />
        </div>
        {fileError && <div className="error-message">{fileError}</div>}
        
        <div className="control-group conditional-arrows">
          <label>
            <input 
              type="checkbox" 
              checked={conditionalArrowsEnabled} 
              onChange={(e) => onToggleConditionalArrows(e.target.checked)} 
            />
            Enable conditional right turn
          </label>
          <div className="tooltip">
            <span className="tooltip-text">
              Allows right turn on red light when vehicles from the left are turning (not going straight)
            </span>
            <span className="info-icon">?</span>
          </div>
        </div>
        
        <button onClick={handleFileUpload}>Run Simulation</button>
      </div>
    </div>
  );
}

export default Controls; 