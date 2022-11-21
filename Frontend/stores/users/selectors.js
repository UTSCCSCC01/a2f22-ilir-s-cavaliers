import { createSelector } from "reselect";

import { userReducerName } from "./adapter";

const userStoreSelector = (state) => state[userReducerName];

export const userDataSelector = createSelector(
  userStoreSelector,
  (userStore) => userStore.userData
);

export const isAuthenticatedSelector = createSelector(
  userStoreSelector,
  (userStore) => userStore.isAuthenticated
);

export const signupSelector = createSelector(
  userStoreSelector,
  (userStore) => userStore.signup
);

export const isSignupPendingSelector = createSelector(
  signupSelector,
  (signup) => signup.isPending
);

export const loginSelector = createSelector(
  userStoreSelector,
  (userStore) => userStore.login
);

export const isLoginPendingSelector = createSelector(
  loginSelector,
  (login) => login.isPending
);

export const logoutSelector = createSelector(
  userStoreSelector,
  (userStore) => userStore.logout
);

export const isLogoutPendingSelector = createSelector(
  logoutSelector,
  (logout) => logout.isPending
);
