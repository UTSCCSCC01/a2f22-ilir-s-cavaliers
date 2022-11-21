import { Badge, Box, Grid, Image, Skeleton } from "@chakra-ui/react";
import moment from "moment";
import React, { useEffect, useState } from "react";
import { getDriverTime } from "../API/trip";

// Dummy images to display
// don't try this at home kids, it's jank
const format = "h:mm A";
const timeOfDays = [
  {
    name: "Morning",
    imageUrl:
      "https://img.freepik.com/free-vector/sleeping-man-driving-car_97231-414.jpg?size=626&ext=jpg",
    imageAlt: "Driving during morning illustration",
  },
  {
    name: "Day",
    imageUrl:
      "https://img.freepik.com/free-vector/familiy-day-outdoors_24908-59725.jpg?size=626&ext=jpg",
    imageAlt: "Driving during day illustration",
  },
  {
    name: "Night",
    imageUrl:
      "https://media.self.com/photos/5fcfe784d5f4012f9329ceb9/2:1/w_1920,h_960,c_limit/AM-Self-Night%20Driving.png",
    imageAlt: "Driving during night illustration",
  },
];

/**
 * Individual card trip card
 * @returns
 */
function TripCard({ trip, isDriver = false }) {
  function formatUnixTime(unixtime) {
    return moment.unix(unixtime).format(format);
  }

  function determineTimeOfDay(unixtime) {
    const time = moment.unix(unixtime).format(format).split(":");
    const hr = parseInt(time[0]);
    const indicator = time[1].slice(3);
    if (hr >= 5 && hr <= 11 && indicator == "AM") {
      return timeOfDays[0];
    } else if (
      (hr >= 11 && indicator == "AM") ||
      (hr <= 5 && indicator == "PM")
    ) {
      return timeOfDays[1];
    }
    return timeOfDays[2];
  }

  const [timeLeft, setTimeLeft] = useState(-1);

  // Grabs the driverTime left if it's a passenger in-progress trip
  useEffect(async () => {
    if (!isDriver && !trip.endTime) {
      const {
        data: {
          data: { arrival_time },
        },
      } = await getDriverTime(trip._id);
      setTimeLeft(`${arrival_time} minutes`);
    }
  }, []);
  const currentTime = determineTimeOfDay(trip.startTime);

  return (
    <Box maxW="sm" borderWidth="1px" borderRadius="lg" overflow="hidden" m="2">
      <Skeleton isLoaded={isDriver || trip.endTime || timeLeft !== -1}>
        <Image
          src={currentTime.imageUrl}
          alt={currentTime.imageAlt}
          h="230px"
          w="100%"
        />
        <Box p="6">
          <Box d="flex" alignItems="baseline">
            {trip.totalCost && (
                <Badge borderRadius="full" px="2" colorScheme="teal">
                  CA ${trip.totalCost}
                </Badge>
              )}
            <Box
              color="gray.500"
              fontWeight="semibold"
              letterSpacing="wide"
              fontSize="xs"
              textTransform="uppercase"
              ml="2"
            >
              Departure: {formatUnixTime(trip.startTime)} &bull;{" "}
              {trip.endTime
                ? `Arrival:${" "}
            ${formatUnixTime(trip.endTime)}`
                : ""}
            </Box>
          </Box>
          <Box
            mt="1"
            fontWeight="semibold"
            as="h4"
            lineHeight="tight"
            isTruncated
          >
            {currentTime.name} drive with{" "}
            {!isDriver
              ? `Driver ${trip.driver}`
              : `Passenger ${trip.passenger}`}
          </Box>
          <Box as="span" ml="2" color="gray.600" fontSize="sm">
            {trip.timeElapsed
              ? `It took ${trip.timeElapsed}`
              : `${timeLeft} until arrival`}
          </Box>
        </Box>
      </Skeleton>
    </Box>
  );
}

/**
 * Full list of trips
 * @returns 
 */
export default function TripsList({ tripInfo, isDriver }) {
  return (
    <Grid templateColumns="repeat(2, 1fr)">
      {tripInfo &&
        tripInfo.map((trip, index) => (
          <TripCard isDriver={isDriver} trip={trip} key={index} />
        ))}
    </Grid>
  );
}
