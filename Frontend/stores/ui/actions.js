import { createAction } from "@reduxjs/toolkit";
import { formatName } from "../helpers";
import { uiReducerName } from "./adapter";

export const closeAllOverlays = createAction(
  formatName(uiReducerName, "closeAllOverlays")
);

export const toggleOverlay = createAction(
  formatName(uiReducerName, "toggleOverlay")
);

export const displayToast = createAction(
  formatName(uiReducerName, "displayToast")
);

const displayToastWrapper = (title, description, isSticky, variant) => {
  return displayToast({
    key: Math.random(),
    title,
    description,
    duration: isSticky ? null : 5000,
    status: variant,
  });
};

export const displaySuccessToast = (title, description, isSticky) => {
  return displayToastWrapper(title, description, isSticky, "success");
};

export const displayErrorToast = (title, description, isSticky) => {
  return displayToastWrapper(title, description, isSticky, "error");
};

export const displayWarningToast = (title, description, isSticky) => {
  return displayToastWrapper(title, description, isSticky, "warning");
};

export const displayInfoToast = (title, description, isSticky) => {
  return displayToastWrapper(title, description, isSticky, "info");
};
