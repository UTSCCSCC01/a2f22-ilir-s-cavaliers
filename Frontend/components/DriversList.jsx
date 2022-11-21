import {
  Box,
  Grid,
  Image,
  Skeleton,
  Button,
  Modal,
  ModalOverlay,
  List,
  ListIcon,
  ModalContent,
  ModalCloseButton,
  FormLabel,
  Input,
  Accordion,
  AccordionItem,
  AccordionPanel,
  AccordionButton,
  AccordionIcon,
  ListItem,
} from "@chakra-ui/react";
import moment from "moment";
import React, { useEffect, useState } from "react";
import { ArrowRightIcon } from "@chakra-ui/icons";
import { useDispatch } from "react-redux";

import { getRoute } from "../API/location";
import { updateTrip } from "../API/trip";
import { displayErrorToast, displaySuccessToast } from "../stores/ui/uiSlice";

/**
 * The modal to end a trip
 */
const EndModal = ({ tripId, closeModal, isOpen, refreshData }) => {

  // State variables to track trip details
  const [distance, setDistance] = useState("0");
  const [endTime, setEndTime] = useState("0");
  const [timeElapsed, setTimeElapsed] = useState("0");
  const [totalCost, setTotalCost] = useState("0");

  //Auxilary hooks
  const [loading, setLoading] = useState(false);
  const dispatch = useDispatch();

  //Actions
  const onClose = () => {
    closeModal();
  };

  const handleSubmit = async () => {
    setLoading(true);
    const {
      data: { status },
    } = await updateTrip(tripId, {
      distance: parseInt(distance),
      endTime: parseInt(endTime),
      timeElapsed: parseInt(timeElapsed),
      totalCost,
    });
    console.log(status);
    setLoading(false);

    if (status == "OK") {
      dispatch(displaySuccessToast("Success!", "Ended trip"));
    } else {
      dispatch(displayErrorToast("Error!", "Could not update information"));
    }
    closeModal();
    await refreshData();
  };

  return (
    <Modal preserveScrollBarGap={true} isOpen={isOpen} onClose={onClose}>
      <ModalOverlay />
      <ModalContent p="10">
        <FormLabel htmlFor="distance">Distance</FormLabel>
        <Input
          id="distance"
          mb={"10px"}
          value={distance}
          onChange={(event) => setDistance(event.target.value)}
        />
        <FormLabel htmlFor="endTime">End Time</FormLabel>
        <Input
          id="endTime"
          mb={"10px"}
          value={endTime}
          onChange={(event) => setEndTime(event.target.value)}
        />
        <FormLabel htmlFor="timeElapsed">Total Time Taken</FormLabel>
        <Input
          id="timeElapsed"
          mb={"10px"}
          value={timeElapsed}
          onChange={(event) => setTimeElapsed(event.target.value)}
        />
        <FormLabel htmlFor="totalCost">Total Cost</FormLabel>
        <Input
          id="totalCost"
          mb={"10px"}
          value={totalCost}
          onChange={(event) => setTotalCost(event.target.value)}
        />
        <Button
          isLoading={loading}
          disabled={
            distance == "0" ||
            endTime == "0" ||
            timeElapsed == "0" ||
            totalCost == "0" ||
            loading
          }
          onClick={handleSubmit}
        >
          Submit
        </Button>
        <ModalCloseButton />
      </ModalContent>
    </Modal>
  );
};

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
 * An individual trip card
 */
function TripCard({ trip, dUid, refreshData }) {
  
  // Format Unit timestamp to string
  function formatUnixTime(unixtime) {
    return moment.unix(unixtime).format(format);
  }

  // Determines the time of day given a unix timestamp
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

  //State variables
  const [route, setRoute] = useState([]);
  const [showModal, setModelVisibility] = useState(false);
  
  //Normal variables
  const currentTime = determineTimeOfDay(trip.startTime);

  // Grab the navigation routes on load
  useEffect(async () => {
    const {
      data: {
        data: { route },
      },
    } = await getRoute(dUid, trip.passenger);
    console.log(route);
    setRoute(route);
  }, []);

  return (
    <Box maxW="sm" borderWidth="1px" borderRadius="lg" overflow="hidden" m="2">
      <Skeleton isLoaded={route.length != 0}>
        <Image
          src={currentTime.imageUrl}
          alt={currentTime.imageAlt}
          h="230px"
          w="100%"
        />
        <Box p="6">
          <Box d="flex" alignItems="baseline">
            <Box
              color="gray.500"
              fontWeight="semibold"
              letterSpacing="wide"
              fontSize="xs"
              textTransform="uppercase"
              ml="2"
            >
              Departure: {formatUnixTime(trip.startTime)}
            </Box>
          </Box>
          <Box
            mt="1"
            fontWeight="semibold"
            as="h4"
            lineHeight="tight"
            isTruncated
          >
            {currentTime.name} drive with Passenger {trip.passenger}
          </Box>
        </Box>
        <Accordion allowToggle>
          <AccordionItem>
            <AccordionButton>
              <Box flex="1" textAlign="left">
                Route
              </Box>
              <AccordionIcon />
            </AccordionButton>
            <AccordionPanel>
              <List spacing={3}>
                {route.map(({ street, has_traffic, time }, index) => (
                  <ListItem key={index}>
                    <ListIcon
                      as={ArrowRightIcon}
                      color={has_traffic ? "red.500" : "green.500"}
                    />{" "}
                    {street}, {time} minutes
                  </ListItem>
                ))}
              </List>
            </AccordionPanel>
          </AccordionItem>
        </Accordion>
        <Button
          mt={"10px"}
          colorScheme={"green"}
          onClick={() => setModelVisibility(true)}
        >
          End Trip
        </Button>
        <EndModal
          refreshData={refreshData}
          tripId={trip._id}
          isOpen={showModal}
          closeModal={() => setModelVisibility((modalOpen) => !modalOpen)}
        />
      </Skeleton>
    </Box>
  );
}

/**
 * A list of Trip Cards
 */
export default function TripsList({ tripInfo, dUid, refreshData }) {
  return (
    <Grid templateColumns="repeat(2, 1fr)">
      {tripInfo &&
        tripInfo.map((trip, index) => (
          <TripCard
            refreshData={refreshData}
            dUid={dUid}
            trip={trip}
            key={index}
          />
        ))}
    </Grid>
  );
}
