import axios from 'axios';

const API_URL = '/api';

class SimulationService {
  runSimulation(commandList) {
    return axios.post(`${API_URL}/run-simulation`, commandList);
  }

  initializeSimulation() {
    return axios.post(`${API_URL}/initialize-simulation`);
  }

  addVehicle(vehicleId, startRoad, endRoad) {
    return axios.post(`${API_URL}/add-vehicle`, {
      type: 'addVehicle',
      vehicleId,
      startRoad,
      endRoad
    });
  }

  executeStep() {
    return axios.post(`${API_URL}/execute-step`);
  }

  getTrafficLightStates() {
    return axios.get(`${API_URL}/traffic-light-states`);
  }

  getWaitingVehicleCounts() {
    return axios.get(`${API_URL}/waiting-vehicles`);
  }

  
  setRightTurnArrowsEnabled(enabled) {
    return axios.post(`${API_URL}/set-right-turn-arrows?enabled=${enabled}`);
  }

  
  setConditionalRightTurnArrowsEnabled(enabled) {
    return axios.post(`${API_URL}/set-conditional-right-turn-arrows?enabled=${enabled}`);
  }

  setMaxVehiclesToProcess(maxVehicles) {
    return axios.post(`${API_URL}/set-max-vehicles?maxVehicles=${maxVehicles}`);
  }
}

const simulationService = new SimulationService();
export default simulationService; 