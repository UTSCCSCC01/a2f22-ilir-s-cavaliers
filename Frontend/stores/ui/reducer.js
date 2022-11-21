import { createReducer } from "@reduxjs/toolkit";

import { closeAllOverlays, displayToast, toggleOverlay } from "./actions";
import { uiAdapter } from "./adapter";

export const initialState = uiAdapter.getInitialState({
  isShowing: {
    login: false,
    signup: false,
  },
  toasts: [],
});

const uiReducer = createReducer(initialState, (builder) => {
  builder.addCase(closeAllOverlays, (state) => {
    state.isShowing = {
      ...initialState.isShowing,
    };
  });
  builder.addCase(displayToast, (state, action) => {
    state.toasts = [...state.toasts, { ...action.payload }];
  });
  builder.addCase(toggleOverlay, (state, { payload }) => {
    if (payload && payload.overlay) {
      state.isShowing[payload.overlay] = !state.isShowing[payload.overlay];
    }
  });
});

export default uiReducer;
