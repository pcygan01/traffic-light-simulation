import React, { useState } from 'react';
import './JsonDisplay.css';

const JsonDisplay = ({ jsonData }) => {
  // Domyślnie rozwinięty
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
        <h3>Wyniki symulacji w formacie JSON</h3>
        <button 
          className="toggle-button" 
          onClick={toggleExpand}
        >
          {isExpanded ? 'Zwiń' : 'Rozwiń'}
        </button>
      </div>
      {isExpanded && (
        <pre className="json-content">{jsonData}</pre>
      )}
    </div>
  );
};

export default JsonDisplay; 