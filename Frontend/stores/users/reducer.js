import { createReducer } from "@reduxjs/toolkit";

import { login, signup } from "./actions";
import { userAdapter } from "./adapter";

export const initialState = userAdapter.getInitialState({
  userData: {},
  isAuthenticated: false,
  login: {
    isPending: false,
    error: null,
  },
  signup: {
    isPending: false,
    error: null,
  },
});

const userReducer = createReducer(initialState, (builder) => {
  builder.addCase(login.pending, (state) => {
    state.login.error = null;
    state.login.isPending = true;
  });
  builder.addCase(login.fulfilled, (state) => {
    state.login.error = null;
    state.isAuthenticated = true;
    state.login.isPending = false;
  });
  builder.addCase(login.rejected, (state, { payload }) => {
    state.login.error = payload;
    state.login.isPending = false;
  });
  builder.addCase(signup.pending, (state) => {
    state.signup.error = null;
    state.signup.isPending = true;
  });
  builder.addCase(signup.fulfilled, (state) => {
    state.signup.error = null;
    state.isAuthenticated = true;
    state.signup.isPending = false;
  });
  builder.addCase(signup.rejected, (state, { payload }) => {
    state.signup.error = payload;
    state.signup.isPending = false;
  });
});

export default userReducer;
