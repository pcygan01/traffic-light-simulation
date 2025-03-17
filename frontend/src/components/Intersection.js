import React from 'react';
import './Intersection.css';

const Intersection = ({ trafficLights, waitingVehicleCounts }) => {
  const getLightClass = (direction, type) => {
    const light = trafficLights[direction];
    if (!light) return '';
    
    if (type === 'red' && light.mainState === 'RED') return 'light-red';
    if (type === 'green' && light.mainState === 'GREEN') return 'light-green';
    
    return '';
  };

  return (
    <div className="intersection">
      {}
      <div className="road road-horizontal"></div>
      <div className="road road-vertical"></div>
      
      {}
      <div className="lane-marker lane-marker-horizontal" style={{ left: '100px' }}></div>
      <div className="lane-marker lane-marker-horizontal" style={{ left: '150px' }}></div>
      <div className="lane-marker lane-marker-horizontal" style={{ left: '200px' }}></div>
      <div className="lane-marker lane-marker-horizontal" style={{ left: '250px' }}></div>
      <div className="lane-marker lane-marker-vertical" style={{ top: '100px' }}></div>
      <div className="lane-marker lane-marker-vertical" style={{ top: '150px' }}></div>
      <div className="lane-marker lane-marker-vertical" style={{ top: '200px' }}></div>
      <div className="lane-marker lane-marker-vertical" style={{ top: '250px' }}></div>
      
      {}
      <div className="traffic-light traffic-light-north">
        <div className={`light ${getLightClass('NORTH', 'red')}`}></div>
        <div className="light"></div>
        <div className={`light ${getLightClass('NORTH', 'green')}`}></div>
      </div>
      
      <div className="traffic-light traffic-light-south">
        <div className={`light ${getLightClass('SOUTH', 'red')}`}></div>
        <div className="light"></div>
        <div className={`light ${getLightClass('SOUTH', 'green')}`}></div>
      </div>
      
      <div className="traffic-light traffic-light-east">
        <div className={`light ${getLightClass('EAST', 'red')}`}></div>
        <div className="light"></div>
        <div className={`light ${getLightClass('EAST', 'green')}`}></div>
      </div>
      
      <div className="traffic-light traffic-light-west">
        <div className={`light ${getLightClass('WEST', 'red')}`}></div>
        <div className="light"></div>
        <div className={`light ${getLightClass('WEST', 'green')}`}></div>
      </div>
      
      {}
      <div className="label label-north">NORTH</div>
      <div className="label label-south">SOUTH</div>
      <div className="label label-east">EAST</div>
      <div className="label label-west">WEST</div>
      
      {}
      <div className="vehicle-counter vehicle-counter-north">
        <div className="counter-value">{waitingVehicleCounts?.NORTH || 0}</div>
        <div className="counter-label">vehicles</div>
      </div>
      <div className="vehicle-counter vehicle-counter-south">
        <div className="counter-value">{waitingVehicleCounts?.SOUTH || 0}</div>
        <div className="counter-label">vehicles</div>
      </div>
      <div className="vehicle-counter vehicle-counter-east">
        <div className="counter-value">{waitingVehicleCounts?.EAST || 0}</div>
        <div className="counter-label">vehicles</div>
      </div>
      <div className="vehicle-counter vehicle-counter-west">
        <div className="counter-value">{waitingVehicleCounts?.WEST || 0}</div>
        <div className="counter-label">vehicles</div>
      </div>
    </div>
  );
};

export default Intersection;
