import { getRequest, patchRequest, putRequest} from "../network";

// The API Gateway URL
const gatewayUrl = process.env.GATEWAY_URL || 'http://localhost:8004'

// The location microservice URL
const locationUrl = process.env.USER_URL || 'http://localhost:8000'


const useGatewayUrl = false;

const url = useGatewayUrl ? gatewayUrl : locationUrl


/**
 * Adds user's initial location
 * @param {String} uid  - the user's id (from location/user microservice)
 * @param {Boolean} isDriver - whether the user is a driver
 */
export const AddUserToLocation = async (uid, isDriver = false) => putRequest(url, "/location/user",{uid, "is_driver": isDriver});

/**
 * Grabs user's location
 * @param {String} uid - The user's id (from location/user microservice)
 * @returns 
 */
export const getUserLocation = async (uid) => getRequest(url,`/location/${uid}`)

/**
 * Sets user's location
 * @param {String} uid - The user's id (from location/user microservice)
 * @param {JSON} data -  A JSON containing the updated location
 * @param {String} data.latitude - the current latitude
 * @param {String} data.longitude - the current longitude
 * @param {String} data.street - the current street
 */
export const setUserLocation = async (uid, data) => patchRequest(url, `/location/${uid}`, data);

/**
 * Get navigation route
 * @param {Number} dUid - the Driver's id (from location/user microservice)
 * @param {Number} pUid - the Passenger's id (from location/user microservice)
 */
export const getRoute = async (dUid, pUid) => getRequest(url, `/location/navigation/${dUid}?passengerUid=${pUid}`);

export default { AddUserToLocation, getUserLocation, setUserLocation, getRoute }