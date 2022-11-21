import { createSelector } from "reselect";

import { uiReducerName } from "./adapter";

const uiStoreSelector = (state) => state[uiReducerName];

export const toastsSelector = createSelector(
  uiStoreSelector,
  (uiStore) => uiStore.toasts
);

export const isShowingSelector = createSelector(
  uiStoreSelector,
  (uiStore) => uiStore.isShowing
);
