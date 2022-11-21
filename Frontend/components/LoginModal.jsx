import {
  Center,
  Modal,
  ModalOverlay,
  ModalContent,
  Heading,
  ModalCloseButton,
  Tab,
  Tabs,
  TabList,
  TabPanel,
  TabPanels,
} from "@chakra-ui/react";
import React, { useEffect, useState } from "react";
import { useDispatch, useSelector } from "react-redux";

import LoginForm from "./LoginForm";
import SignupForm from "./SignupForm";

import { closeAllOverlays, isShowingSelector } from "../stores/ui/uiSlice";

//Login and signup Modal
export const AccessModal = () => {
  const [tabIndex, setTabIndex] = useState(0);

  const { login: isLoginVisible, signup: isSignupVisible } = useSelector(
    isShowingSelector
  );
  const dispatch = useDispatch();

  //Changes initial tab
  useEffect(() => {
    if (isLoginVisible) {
      setTabIndex(0);
    } else {
      setTabIndex(1);
    }
  }, [isLoginVisible, setTabIndex, dispatch]);

  const handleTabsChange = (index) => {
    setTabIndex(index);
  };

  return (
    <>
      <Modal
        preserveScrollBarGap={true}
        isOpen={isLoginVisible || isSignupVisible}
        onClose={() => dispatch(closeAllOverlays())}
      >
        <ModalOverlay />
        <ModalContent p="4">
          <Tabs
            index={tabIndex}
            variant="soft-rounded"
            colorScheme="green"
            onChange={handleTabsChange}
          >
            <TabList justifyContent="center" m={4}>
              <Tab mr="2">Login</Tab>
              <Tab ml="2">Signup</Tab>
            </TabList>
            <TabPanels>
              <TabPanel>
                <Center>
                  <Heading as="h2" size="lg">
                    Login
                  </Heading>
                </Center>
                <LoginForm />
              </TabPanel>
              <TabPanel>
                <Center>
                  <Heading as="h2" size="lg">
                    Signup
                  </Heading>
                </Center>
                <SignupForm />
              </TabPanel>
            </TabPanels>
          </Tabs>
          <ModalCloseButton />
        </ModalContent>
      </Modal>
    </>
  );
};

export default AccessModal;
