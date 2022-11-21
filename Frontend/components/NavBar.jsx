import {
  Box,
  Button,
  Flex,
  Heading,
  HStack,
  IconButton,
  Spacer,
  useColorMode,
  useColorModeValue,
} from "@chakra-ui/react";
import Link from "next/link";
import React from "react";
import { FaMoon, FaSun } from "react-icons/fa";
import { useDispatch, useSelector } from "react-redux";

import { retrieveFromCookie, validateCookie } from "../network";
import { toggleOverlay } from "../stores/ui/uiSlice";
import { isAuthenticatedSelector } from "../stores/users/userSlice";

const navBtns = ["Trips", "Profile", "Logout"];

//Displays Navigation buttons
const NavButtons = ({ isLoggedIn, toggleOverlay, isDriver }) => {
  return (
    <>
      {isLoggedIn ? (
        <>
          {navBtns.map((btn) => (
            <Button key={btn} size="sm" variant="link" mb={2}>
              <Link href={`/${btn.toLowerCase()}`}>{btn}</Link>
            </Button>
          ))}
          {isDriver ? (
            <Button key={"driver"} size="sm" variant="link" mb={2}>
              <Link href={`/driver`}>{"Driver"}</Link>
            </Button>
          ) : null}
        </>
      ) : (
        <HStack>
          <Button
            variant="solid"
            colorScheme="blue"
            onClick={() => toggleOverlay({ overlay: "login" })}
          >
            Login
          </Button>
          <Button
            colorScheme="green"
            onClick={() => toggleOverlay({ overlay: "signup" })}
          >
            Signup
          </Button>
        </HStack>
      )}
    </>
  );
};

const Navbar = () => {
  const { toggleColorMode } = useColorMode();
  const SwitchIcon = useColorModeValue(FaMoon, FaSun);
  const nextMode = useColorModeValue("dark", "light");

  const isAuthenticated = useSelector(isAuthenticatedSelector);
  const dispatch = useDispatch();

  const isLoggedIn = isAuthenticated || validateCookie();
  const isDriver = retrieveFromCookie("isDriver") === "true";
  return (
    <Flex w="100%" m="2">
      <Box m="2">
        <Heading size="md">
          <Link href="/">Zoomer</Link>
        </Heading>
      </Box>
      <Spacer />
      <HStack spacing="24px">
        <NavButtons
          isDriver={isDriver}
          isLoggedIn={isLoggedIn}
          toggleOverlay={(args) => dispatch(toggleOverlay(args))}
        />
        <IconButton
          size="md"
          fontSize="lg"
          aria-label={`Switch to ${nextMode} mode`}
          variant="ghost"
          color="current"
          margin="1"
          mr="4"
          onClick={toggleColorMode}
          icon={<SwitchIcon />}
        />
      </HStack>
    </Flex>
  );
};

export default Navbar;
