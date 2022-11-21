import { Box, Divider, HStack, Link, Text } from "@chakra-ui/react";
import React from "react";

/**
 * Footer
 */
export default function Footer() {
  return (
    <Box
      as="footer"
      mt={6}
      height="100%"
      width="100%"
      textAlign="center"
      className="app"
    >
      <Divider mb={6} colorScheme="gray" width="100%" />
      <HStack spacing="100px" justifyContent="center">
        <Link
          color="#2c98f0"
          isExternal
          href="https://www.youtube.com/watch?v=SeMXa5lBGYc"
        >
          Terms
        </Link>
        <Link color="#2c98f0" isExternal href="https://youtu.be/G1sfvkk6vAA">
          Privacy
        </Link>
        <Link color="#2c98f0" isExternal href="https://youtu.be/KC6T3_O2iWc">
          Contact
        </Link>
        <Link color="#2c98f0" isExternal href="https://youtu.be/jfZOvQnsBq0">
          Blog
        </Link>
        <Link color="#2c98f0" isExternal href="https://youtu.be/EijpUzjKeM8">
          About
        </Link>
      </HStack>
      <Text fontSize="sm" m="2">
        © 2021 Zoomer Technologies Inc.
      </Text>
    </Box>
  );
}
