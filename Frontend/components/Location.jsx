import { useState, useEffect } from "react";
import {
  Input,
  FormLabel,
  useColorModeValue,
  Button,
  Container,
  Flex,
  Select,
  Heading,
} from "@chakra-ui/react";
import { useDispatch } from "react-redux";

import { getUserLocation, setUserLocation } from "../API/location";
import { displaySuccessToast, displayErrorToast } from "../stores/ui/uiSlice";
import { Roads } from "../testing/mockData";

/**
 * Display and interact with user's location
 */
export const Location = ({ uid }) => {

  //State variables
  const [curLatitude, setLat] = useState("0");
  const [curLongitude, setLong] = useState("0");
  const [curStreet, setStreet] = useState("");

  const dispatch = useDispatch();


  //Actions
  const handleLongChange = (event) => setLong(event.target.value);
  const handleLetChange = (event) => setLat(event.target.value);
  const handleStreetChange = (event) => setStreet(event.target.value);

  //Action to set the user's new location
  const handleSubmit = async () => {
    const { status } = await setUserLocation(uid, {
      latitude: curLatitude,
      longitude: curLongitude,
      street: curStreet,
    });
    if (status == 200) {
      dispatch(displaySuccessToast("Success!", "Updated your location"));
    } else {
      dispatch(displayErrorToast("Error!", "Could not update location"));
    }
  };

  //Grab user's location
  useEffect(async () => {
    const {
      data: {
        data: { latitude, longitude, street },
      },
    } = await getUserLocation(uid);
    setLat(latitude);
    setLong(longitude);
    setStreet(street);
  }, []);

  return (
    <Flex align={"center"} justify={"center"}>
      <Container
        maxW={"lg"}
        bg={useColorModeValue("white", "whiteAlpha.100")}
        boxShadow={"xl"}
        rounded={"lg"}
        p={6}
        direction={"column"}
      >
        <Heading mb={"10px"} size={"xs"}>
          We respect your privacy, so we ask for your location (unlike others)
        </Heading>
        <FormLabel htmlFor="latitude">Latitude</FormLabel>
        <Input value={curLatitude} onChange={handleLetChange} />
        <FormLabel htmlFor="longitude">Longitude</FormLabel>
        <Input value={curLongitude} onChange={handleLongChange} />
        <FormLabel htmlFor="longitude">Current Street</FormLabel>
        <Select
          variant="filled"
          value={curStreet}
          onChange={handleStreetChange}
        >
          {Roads.map((street, index) => (
            <option value={street} key={index}>
              {street}
            </option>
          ))}
        </Select>
        <Button onClick={handleSubmit} mt={"10px"} colorScheme={"green"}>
          Update Location
        </Button>
      </Container>
    </Flex>
  );
};

export default Location;
