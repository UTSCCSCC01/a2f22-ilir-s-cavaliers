import { createAsyncThunk } from "@reduxjs/toolkit";
import { displaySuccessToast, displayErrorToast } from "../ui/uiSlice";
import { userReducerName } from "./adapter";
import { deleteUserCookie, setUserCookie } from "../../network";
import { loginUser, registerUser, getUserInformation } from "../../API/user";
import { AddUserToLocation } from '../../API/location';
import { formatName } from "../helpers";

export const login = createAsyncThunk(
  formatName(userReducerName, "login"),
  async (payload, { dispatch, rejectWithValue }) => {
    const email = payload.email;
    const password = payload.password;
    try {
      const {data} = await loginUser(email,password);
      if (data.uid == undefined){
        throw new Error("uid not returned");
      }

      //Fetch data
      const { data: {data: {rides, isDriver}} } = await getUserInformation(data.uid);

      const userData = {
        uid: data.uid,
        email,
        rides: parseInt(rides),
        isDriver
      };
      setUserCookie(userData);
      dispatch(displaySuccessToast("Login successful", `Happy Zooming! User ${data.uid}`));
      return true;
    } catch (e) {
      dispatch(displayErrorToast("Error has occured", "Login unsuccessful."));
      return rejectWithValue({
        status: e.response && e.response.status,
        message: e.response && e.response.data,
      });
    }
  }
);

export const signup = createAsyncThunk(
  formatName(userReducerName, "signup"),
  async (payload, { dispatch, rejectWithValue }) => {
    const email = payload.email;
    const fullName = payload.fullName;
    const password = payload.password;

    try {
      const {data} = await registerUser(email,fullName, password);
      if (data.uid == undefined){
        throw new Error("uid not returned");
      }
      const userData = {
        uid: data.uid,
        email,
        rides: 0,
        isDriver: false //TODO
      };

      // Add user to locations
      await AddUserToLocation(data.uid, userData.isDriver)

      setUserCookie(userData);
      dispatch(displaySuccessToast("Signup successful", `Happy Zooming! User ${data.uid}`));
      return true;
    } catch (e) {
      dispatch(displayErrorToast("Error has occured", "Signup unsuccessful."));
      return rejectWithValue({
        status: e.response && e.response.status,
        message: e.response && e.response.data,
      });
    }
  }
);

export const patchUser = createAsyncThunk(
  formatName(userReducerName, "patchUser"),
  async (
    { uid, email, password, rides },
    { dispatch, rejectWithValue }
  ) => {
    let update = {};
    if (!uid) return false;
    if (email) update.email = email;
    if (password) update.password = password;
    if (rides) update.rides = rides;

    try {
      /*
      const response = await patchRequest(`/api/user/${uid}`, userData);
      return response.data;*/
      dispatch(displaySuccessToast("Change successful", ""));
      return true;
    } catch (e) {
      dispatch(displayErrorToast("Error has occured", "Signup unsuccessful."));
      return rejectWithValue({
        status: e.response && e.response.status,
        message: e.response && e.response.data,
      });
    }
  }
);
