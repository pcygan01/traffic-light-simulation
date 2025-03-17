import React from 'react';
import './TrafficLight.css';

const TrafficLight = ({ direction, state, rightTurnArrow }) => {
  return (
    <div className={`traffic-light traffic-light-${direction.toLowerCase()}`}>
      <div className="traffic-light-label">{direction}</div>
      <div className="traffic-light-signals">
        <div className={`signal red ${state === 'RED' ? 'active' : ''}`}></div>
        <div className={`signal yellow ${state === 'YELLOW' ? 'active' : ''}`}></div>
        <div className={`signal green ${state === 'GREEN' ? 'active' : ''}`}></div>
        {rightTurnArrow && (
          <div className="signal green-arrow active">→</div>
        )}
      </div>
    </div>
  );
};

export default TrafficLight; 