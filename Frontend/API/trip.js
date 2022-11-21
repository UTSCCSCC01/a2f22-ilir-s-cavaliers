import { getRequest, patchRequest, postRequest } from "../network";

// The API gateway url
const gatewayUrl = process.env.GATEWAY_URL || 'http://localhost:8004'

// The Trip microservice url
const tripUrl = process.env.USER_URL || 'http://localhost:8002'


const useGatewayUrl = false;

const url = useGatewayUrl ? gatewayUrl : tripUrl

/**
 * Grab all trips the user was a passenger for
 * @param {String} uid - The user's id (from location/user microservice)
 */
export const getPassengerTrips = (uid) => getRequest(url,`/trip/passenger/${uid}`);

/**
 * Grab all trips the user was a driver for
 * @param {String} uid  - The user's id (from location/user microservice)
 */
export const getDriverTrips = (uid) => getRequest(url,`/trip/driver/${uid}`);

/**
 * Requests for drivers to initiate a trip
 * @param {*} uid - The user's id (from location/user microservice)
 * @param {*} radius - The radius to look for
 */
export const requestTrip = (uid, radius) => postRequest(url, `/trip/request`,{uid, radius: parseInt(radius)});

/**
 * Confirms a trip, creates the trip between driver and passenger
 * @param {String} dUid - The driver's id (from location/user microservice)
 * @param {String} pUid - The passenger's id (from location/user microservice)
 * @param {Number} startTime - The start time, UNIX time stamp
 */
export const confirmTrip = (dUid, pUid, startTime) => postRequest(url, `/trip/confirm`, {driver: dUid, passenger: pUid, startTime});

/**
 * Grab the time it takes for the driver to arrive at your location
 * @param {String} tUid - The trip's id (from the trip microservice) 
 */
export const getDriverTime = (tUid) => getRequest(url, `/trip/driverTime/${tUid}`);

/**
 * Updates Trip information
 * @param {String} tripId - The trip's id (from the trip microservice) 
 * @param {JSON} data - the updated trip information
 * @param {Number} data.distance - the distance travelled
 * @param {Number} data.totalCost - the total cost of the trip
 * @param {Number} data.endTime - the time of drop off, UNIX timestamp
 */
export const updateTrip = (tripId, data) => patchRequest(url, `/trip/${tripId}`, data);

export default { getPassengerTrips, requestTrip, confirmTrip, getDriverTrips, getDriverTime, updateTrip };