import { Heading } from "@chakra-ui/react";
import React, { useEffect, useState } from "react";

import AuthCheck from "../../components/AuthCheck";
import TripsList from "../../components/TripsList";
import Location from '../../components/Location';
import RequestTrip from "../../components/RequestTrip";

import { getPassengerTrips } from '../../API/trip';
import { retrieveFromCookie } from '../../network';

/**
 * Passenger drips, grabs all trips the user was a passenger for
 */
const Trips = () => {

  const [currentTrips, setCurrentTrips] = useState(null);
  const [oldTrips, setOldTrips] = useState([]);
  const uid = retrieveFromCookie('uid');

  // Re-usable function to grab all passenger trips and sets accordingly
  const fetchData = async () => {
    const { data: { data: {trips} } } =  await getPassengerTrips(uid);
    console.log(trips);
   const curOldTrips = trips.filter(({ distance }) => distance != undefined)
   const curNewTrips = trips.filter(({ distance }) => distance == undefined)
   setOldTrips(curOldTrips);
   setCurrentTrips(curNewTrips);
  }

  //Grabs new data on load
  useEffect(async () => {
    try {
      await fetchData();
    } catch (error) {
      console.log(error);
    }
  }, [])

  return (
    <>
      <AuthCheck />
      <div className="container">
        <Heading>Your Location</Heading>
        <Location uid={uid}/>
        <RequestTrip refreshData={fetchData} uid={uid}/>
        {
          currentTrips &&
          (<>
          <Heading>Your Current Trips</Heading>
          <TripsList tripInfo={currentTrips} />
          </>)
        }

        <Heading>Your Old Trips</Heading>
        <TripsList tripInfo={oldTrips} />
      </div>
    </>
  );
}

export default Trips;
