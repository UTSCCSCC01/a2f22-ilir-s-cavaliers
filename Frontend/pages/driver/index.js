import { Heading } from "@chakra-ui/react";
import React, { useState, useEffect } from "react";

import AuthCheck from "../../components/AuthCheck";
import TripsList from "../../components/TripsList";
import DriverTrips from "../../components/DriversList";
import Location from "../../components/Location";

import { retrieveFromCookie } from "../../network";
import { getDriverTrips } from "../../API/trip";


const Trips = () => {
  const [currentTrips, setCurrentTrips] = useState(null);
  const [oldTrips, setOldTrips] = useState([]);
  const uid = retrieveFromCookie("uid");

  // Re-usable function to grab new driver trips
  const fetchData = async () => {
    const {
      data: {
        data: { trips },
      },
    } = await getDriverTrips(uid);
    console.log(trips);
    const curOldTrips = trips.filter(({ distance }) => distance != undefined);
    const curNewTrips = trips.filter(({ distance }) => distance == undefined);
    setOldTrips(curOldTrips);
    setCurrentTrips(curNewTrips);
  };

  // Grab new Data on load
  useEffect(async () => {
    try {
      await fetchData();
    } catch (error) {
      console.log(error);
    }
  }, []);

  return (
    <>
      <AuthCheck />
      <div className="container">
        <Heading>Your Location</Heading>
        <Location uid={uid} />
        {currentTrips && (
          <>
            <Heading>Your Current Trips</Heading>
            <DriverTrips
              refreshData={fetchData}
              dUid={uid}
              tripInfo={currentTrips}
            />
          </>
        )}

        <Heading>Your Old Trips</Heading>
        <TripsList isDriver={true} tripInfo={oldTrips} />
      </div>
    </>
  );
};

export default Trips;
