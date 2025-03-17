import React from 'react';
import './Vehicle.css';

const Vehicle = ({ id, startRoad, endRoad, position, isMoving, isBlocked, turnDirection }) => {
  const getPosition = () => {
    const baseOffset = position * 70; 
    
    switch (startRoad) {
      case 'NORTH':
        return { 
          bottom: `${180 + baseOffset}px`, 
          left: '170px',
          transform: 'rotate(180deg)' 
        };
      case 'SOUTH':
        return { 
          top: `${180 + baseOffset}px`, 
          left: '230px',
          transform: 'rotate(0deg)' 
        };
      case 'EAST':
        return { 
          top: '230px', 
          left: `${180 + baseOffset}px`,
          transform: 'rotate(270deg)' 
        };
      case 'WEST':
        return { 
          top: '170px', 
          right: `${180 + baseOffset}px`,
          transform: 'rotate(90deg)' 
        };
      default:
        return {};
    }
  };

  const getVehicleClass = () => {
    let classes = 'vehicle';
    
    if (isMoving) {
      classes += ` vehicle-moving-${startRoad.toLowerCase()}`; 
    }
    
    if (turnDirection === 'LEFT') {
      classes += ' vehicle-turning-left';
    } else if (turnDirection === 'RIGHT') {
      classes += ' vehicle-turning-right';
    } else {
      classes += ' vehicle-going-straight';
    }
    
    if (isBlocked) {
      classes += ' vehicle-blocked';
    }
    
    return classes;
  };

  const getVehicleIcon = () => {
    if (turnDirection === 'LEFT') return '↰';
    if (turnDirection === 'RIGHT') return '↱';
    return '⬆️';
  };

  const getDisplayId = () => {
    const match = id.match(/\d+/);
    return match ? match[0] : id;
  }
 //Zostawiam też wszystkie rzeczy powiązane z wizualizacją pojazdu, jeszcze do niej wrócę!
  return (
    <div 
      className={getVehicleClass()} 
      style={getPosition()}
      title={`Vehicle ${id}: ${startRoad} to ${endRoad} (${turnDirection})`}
    >
      <div className="vehicle-body">
        <div className="vehicle-id">{getDisplayId()}</div>
        <div className="vehicle-icon">{getVehicleIcon()}</div>
      </div>
      <div className="vehicle-status">
        {isMoving ? '🚦' : (isBlocked ? '⛔' : '⏳')} 
      </div>
    </div>
  );
};

export default Vehicle;
