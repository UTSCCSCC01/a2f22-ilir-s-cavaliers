import React, { useEffect, useState } from "react";
import { Heading } from "@chakra-ui/react";

import AuthCheck from "../../components/AuthCheck";

import { getUserInformation } from "../../API/user";
import { retrieveFromCookie } from "../../network";

/**
 * Profile component, displays all the information about a user
 */
export default function Profile() {
  const [userData, setUserData] = useState({
    rides: 0,
    email: "",
    name: "",
    isDriver: false,
  });

  // Grabs profile data on load
  useEffect(async () => {
    const uid = retrieveFromCookie("uid");
    const {
      data: { data: { rides, email, name, isDriver }},
    } = await getUserInformation(uid);
    setUserData({ rides: parseInt(rides), name, email, isDriver });
  }, []);

  return (
    <>
      <AuthCheck />
      <div className="container">
        <Heading>Full Name</Heading>
        {userData.name}
        <Heading>Email</Heading>
        {userData.email}
        <Heading>You have participated in {userData.rides} rides!</Heading>
        <Heading>
          You are {userData.isDriver ? "a driver!" : "not a driver!"}
        </Heading>
      </div>
    </>
  );
}
