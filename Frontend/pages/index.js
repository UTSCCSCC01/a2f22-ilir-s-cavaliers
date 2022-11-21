import React from "react";
import { useSelector } from "react-redux";

import { validateCookie } from "../network";
import Trips from "./trips";
import Landing from "../components/views/Landing";
import { isAuthenticatedSelector } from "../stores/users/userSlice";

function Home() {
  /**
   * Redux Properties
   */
  const isAuthenticated = useSelector(isAuthenticatedSelector);

  /**
   * Local properties
   */
  const isLoggedIn = isAuthenticated || validateCookie();


  return <>{isLoggedIn ? <Trips /> : <Landing />}</>;
}

export default Home;
