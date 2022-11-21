import { applyMiddleware, combineReducers, createStore } from "redux";
import { composeWithDevTools } from "redux-devtools-extension/developmentOnly";
import thunk from "redux-thunk";

import { uiReducerName } from "./ui/adapter";
import uiReducer from "./ui/reducer";
import { userReducerName } from "./users/adapter";
import userReducer from "./users/reducer";

const createRootReducer = () =>
  combineReducers({
    [uiReducerName]: uiReducer,
    [userReducerName]: userReducer,
  });

export default function configureStore(preloadedState) {
  const store = createStore(
    createRootReducer(),
    preloadedState,
    composeWithDevTools(applyMiddleware(thunk))
  );
  return store;
}
