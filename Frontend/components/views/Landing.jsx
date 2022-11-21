import { Heading, Grid, GridItem } from "@chakra-ui/react";
import React from "react";

import LoginModal from "../../components/LoginModal";

/**
 * The Landing page
 */
export default function Landing() {
  return (
    <Grid
      className="container"
      style={{ backgroundColor: "#b1daff" }}
      templateRows="repeat(1, 1fr)"
      templateColumns="repeat(6,1fr)"
    >
      <LoginModal />
      <GridItem rowSpan={1} colSpan={2} textAlign="center">
        <Heading as="h1" size="3xl">
          Zoomer
        </Heading>
        <Heading as="h1">Rideshare like a local.</Heading>
      </GridItem>
      <GridItem colSpan={4}>
        <img
          src="https://cdn.dribbble.com/users/330915/screenshots/6720147/4_share_dribbble.gif"
          alt="Motorcycle animation"
        />
      </GridItem>
    </Grid>
  );
}
