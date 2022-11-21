import { getRequest, patchRequest, postRequest} from "../network";

// The API gateway URL
const gatewayUrl = process.env.GATEWAY_URL || 'http://localhost:8004'

//The user microservice URL
const userUrl = process.env.USER_URL || 'http://localhost:8001'

const useGatewayUrl = false;

const url = useGatewayUrl ? gatewayUrl : userUrl

/**
 * Logs a user in
 * @param {String} email - The user's email
 * @param {String} password - The user's password
 */
export const loginUser = async (email, password) => postRequest(url,'/user/login', {email, password}); 

/**
 * Registers a new user
 * @param {String} email - The user's email
 * @param {String} name - The user's name
 * @param {String} password - The user's password
 * @returns 
 */
export const registerUser = async (email, name, password) => postRequest(url,'/user/register', {email, name, password}); 

/**
 * Update user ride counter
 * @param {String} uid - the user's id (from location/user microservice)
 * @param {Number} rides - the new ride count
 * @returns 
 */
export const updateUserRides = async (uid, rides) => patchRequest(url, `/user/${uid}`, {rides})

/**
 * 
 * @param {*} uid 
 * @returns 
 */
export const getUserInformation = async (uid) => getRequest(url,`/user/${uid}`);


export default { loginUser, registerUser, getUserInformation, updateUserRides }