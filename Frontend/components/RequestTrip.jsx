import {
  Heading,
  Button,
  Modal,
  ModalOverlay,
  ModalContent,
  ModalCloseButton,
  Tabs,
  Tab,
  TabList,
  TabPanel,
  TabPanels,
  Input,
  FormLabel,
  Select,
} from "@chakra-ui/react";
import { useEffect, useState } from "react";
import Cookies from "js-cookie";
import { useDispatch } from "react-redux";

import { displayErrorToast, displaySuccessToast } from "../stores/ui/uiSlice";
import { updateUserRides } from "../API/user";
import { requestTrip, confirmTrip } from "../API/trip";
import { retrieveFromCookie } from "../network";

/**
 * The modal to request for a trip
 */
const RequestModal = ({ uid, closeModal, isOpen, refreshData }) => {

  //State variables
  const [curTab, setTab] = useState(0);
  const [curRadius, setRadius] = useState("0");

  const dispatch = useDispatch();

  //Close action
  const onClose = () => {
    setTab(0);
    closeModal();
  };

  //The different pages to render
  const PageOne = () => (
    <>
      <FormLabel htmlFor="radius">Radius</FormLabel>
      <Input
        id="radius"
        mb={"10px"}
        value={curRadius}
        onChange={(event) => setRadius(event.target.value)}
      />
      <Button onClick={() => setTab(1)}>Next</Button>
    </>
  );

  const PageTwo = () => {
    const [drivers, setDrivers] = useState([]);
    const [curDriver, setCurDriver] = useState(0);
    const rides = retrieveFromCookie("rides");

    //Creates a trip, and closes the modal
    const handleSubmit = async () => {
      const { status, data: { data: {_id: {$oid}}} } = await confirmTrip(curDriver, uid, Math.floor(Date.now() / 1000));

      if (status == 200) {
        const _ = await updateUserRides(uid, parseInt(rides) + 1);
        Cookies.set("rides", parseInt(rides) + 1);
        dispatch(displaySuccessToast("Success!", `Requested a ride with ID: ${$oid}`));
        setTab(0);
        closeModal();
        await refreshData();
      } else {
        setTab(0);
        dispatch(displayErrorToast("Error", "Could not request a ride"));
        closeModal();
      }
    };

    // Grabs all drivers within the given radius
    useEffect(async () => {
      try {
        const {
          data: { data },
        } = await requestTrip(uid, curRadius);
        const driversNearby = data
          .map((driverId) => ({ driverId }))
          .filter(({ driverId }) => driverId !== uid);
        setDrivers(driversNearby);
        if (driversNearby.length > 0) {
          setCurDriver(driversNearby[0].driverId);
        }
      } catch (error) {
        setDrivers([]);
      }
    }, []);

    return (
      <>
        {drivers.length > 0 ? (
          <>
            <Select
              value={curDriver}
              onChange={(event) => setCurDriver(event.target.value)}
            >
              {drivers.map(({ driverId }) => (
                <option value={driverId}>Driver {driverId}</option>
              ))}
            </Select>
            <Button mt={"10px"} onClick={handleSubmit}>
              Submit
            </Button>
          </>
        ) : (
          "No Drivers Nearby"
        )}
      </>
    );
  };

  return (
    <Modal preserveScrollBarGap={true} isOpen={isOpen} onClose={onClose}>
      <ModalOverlay />
      <ModalContent p="10">
        <Tabs isLazy index={curTab} isFitted variant="enclosed">
          <TabList mb="1em">
            <Tab>Select Radius</Tab>
            <Tab>Choose Driver</Tab>
          </TabList>
          <TabPanels>
            <TabPanel>
              <PageOne />
            </TabPanel>
            <TabPanel>
              <PageTwo />
            </TabPanel>
          </TabPanels>
        </Tabs>
        <ModalCloseButton />
      </ModalContent>
    </Modal>
  );
};

// Displays accompanying button and renders modal accordingly
export const RequestTrip = ({ uid, refreshData }) => {
  const [showModal, setModelVisibility] = useState(false);

  const handleButtonClick = (event) =>
    setModelVisibility((modalOpen) => !modalOpen);

  return (
    <>
      <Heading>Request a Ride!</Heading>
      <Button onClick={handleButtonClick}>Press to Request a ride</Button>
      <RequestModal
        refreshData={refreshData}
        uid={uid}
        isOpen={showModal}
        closeModal={() => setModelVisibility((modalOpen) => !modalOpen)}
      />
    </>
  );
};

export default RequestTrip;
