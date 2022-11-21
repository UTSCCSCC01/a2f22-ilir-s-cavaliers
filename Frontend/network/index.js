import axios from "axios";
import Cookies from "js-cookie";

// Enforces whether a url should end with a /
const enforceTrailingSlash = (url) => {
  return url.endsWith("/") || url.includes("?") ? url : url + "/";
};

const config = {};

// Performs a get call
export const getRequest = async (url, uri, searchParams) => {
  const params = searchParams ? searchParams : "";
  return axios.get(enforceTrailingSlash(`${url}${uri}`) + params, config);
};

// Performs a delete call
export const deleteRequest = async (url, uri) =>
  axios.delete(enforceTrailingSlash(`${url}${uri}`), config);

  // Performs a patch call
export const patchRequest = async (url, uri, data) =>
  axios.patch(enforceTrailingSlash(`${url}${uri}`), data, config);

// Performs a post call
export const postRequest = async (url, uri, data) =>
  axios.post(enforceTrailingSlash(`${url}${uri}`), data, config);

  // Performs a put request
export const putRequest = async (url, uri, data) =>
  axios.put(enforceTrailingSlash(`${url}${uri}`), data, config);

// Grabs an entry from the Cookie
export function retrieveFromCookie(key) {
  return Cookies.get(key);
}

// Retrieves all values from the cookie
export function getCookie() {
  return {
    uid: retrieveFromCookie("uid"),
    email: retrieveFromCookie("email"),
    isDriver: retrieveFromCookie("isDriver"),
    rides: retrieveFromCookie("rides"),
  };
}

// Sets the default cookie structure
export function setUserCookie(userData) {
  const { uid, email, isDriver, rides } = userData;
  Cookies.set("uid", uid);
  Cookies.set("email", email);
  Cookies.set("isDriver", isDriver);
  Cookies.set("rides", rides);
}

// Validates whether a previous cookie is correct
export function validateCookie() {
  const { uid, email, rides, isDriver } = getCookie();
  return uid && email && rides != undefined && isDriver != undefined;
}

// Deletes the current cookie
export function deleteUserCookie() {
  Cookies.remove("uid");
  Cookies.remove("email");
  Cookies.remove("rides");
  Cookies.remove("isDriver");
}
