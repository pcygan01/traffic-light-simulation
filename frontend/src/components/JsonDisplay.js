import React, { useState } from 'react';
import './JsonDisplay.css';

const JsonDisplay = ({ jsonData }) => {
  const [isExpanded, setIsExpanded] = useState(true);

  if (!jsonData) {
    return null;
  }

  const toggleExpand = () => {
    setIsExpanded(!isExpanded);
  };

  return (
    <div className="json-display">
      <div className="json-header">
        <h3>Simulation results in JSON format</h3>
        <button 
          className="toggle-button" 
          onClick={toggleExpand}
        >
          {isExpanded ? 'Collapse' : 'Expand'}
        </button>
      </div>
      {isExpanded && (
        <pre className="json-content">{jsonData}</pre>
      )}
    </div>
  );
};

export default JsonDisplay; 