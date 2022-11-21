import {
  Button,
  Input,
  InputGroup,
  InputRightElement,
  FormControl,
  FormLabel,
  FormHelperText,
} from "@chakra-ui/react";
import { Form, withFormik } from "formik";
import React, { useState } from "react";
import { AiFillEyeInvisible, AiFillEye } from "react-icons/ai";
import { useDispatch, useSelector } from "react-redux";
import * as Yup from "yup";

import { closeAllOverlays } from "../stores/ui/uiSlice";
import { signup, isSignupPendingSelector } from "../stores/users/userSlice";

// Signup validaton schema
export const SignupSchema = Yup.object().shape({
  fullName: Yup.string().required(),
  email: Yup.string().email().required(),
  password: Yup.string().required(),
});

// Basic signup component
export function SignupForm({
  closeAllOverlays,
  errors,
  handleSubmit,
  isPending,
  setFieldValue,
  values: { fullName, email, password },
}) {
  const [show, setShow] = useState(false);
  const handleClick = () => setShow(!show);
  return (
    <Form>
      <FormControl id="email" mt={2}>
        <FormLabel>Email address</FormLabel>
        <Input
          value={email}
          onChange={(e) => setFieldValue("email", e.target.value)}
          placeholder="Enter email address"
        />
      </FormControl>
      <FormHelperText style={{ color: "red" }}>
        {errors && errors["email"]}
      </FormHelperText>
      <FormControl id="fullName" mt={2}>
        <FormLabel>Full Name</FormLabel>
        <Input
          value={fullName}
          onChange={(e) => setFieldValue("fullName", e.target.value)}
          placeholder="Enter full name"
        />
      </FormControl>
      <FormHelperText style={{ color: "red" }}>
        {errors && errors["fullName"]}
      </FormHelperText>
      <FormControl id="password" mt={2}>
        <FormLabel>Password</FormLabel>
        <InputGroup size="md">
          <Input
            onChange={(e) => setFieldValue("password", e.target.value)}
            value={password}
            pr="4.5rem"
            type={show ? "text" : "password"}
            placeholder="Enter password"
          />
          <InputRightElement width="4.5rem">
            <Button variant="ghost" h="1.75rem" size="sm" onClick={handleClick}>
              {show ? <AiFillEyeInvisible /> : <AiFillEye />}
            </Button>
          </InputRightElement>
        </InputGroup>
        <FormHelperText style={{ color: "red" }}>
          {errors && errors["password"]}
        </FormHelperText>
        <FormHelperText>
          Click on the eye icon to peek at your password.
        </FormHelperText>
      </FormControl>
      <div>
        <Button
          isLoading={isPending}
          m={4}
          type="submit"
          onClick={handleSubmit}
          style={{ float: "right" }}
          colorScheme="green"
        >
          Submit
        </Button>
        <Button
          m={4}
          variant="outline"
          onClick={closeAllOverlays}
          style={{ float: "right" }}
        >
          Close
        </Button>
      </div>
    </Form>
  );
}
// Signup form with validation (See Formik Guides)

export const EnhancedSignupForm = withFormik({
  enabledReinitialize: true,
  handleSubmit: ({ email, fullName, password }, { props: { signup } }) => {
    signup({ email, fullName, password });
  },
  mapPropsToValues: (props) => ({
    email: "",
    fullName: "",
    password: "",
  }),
  validationSchema: () => SignupSchema,
  validateOnBlur: false,
  validateOnChange: false,
})(SignupForm);

const ConnectedSignupForm = () => {
  const isPending = useSelector(isSignupPendingSelector);
  const dispatch = useDispatch();
  return (
    <EnhancedSignupForm
      isPending={isPending}
      closeAllOverlays={() => dispatch(closeAllOverlays())}
      signup={(args) => dispatch(signup(args))}
    />
  );
};

export default ConnectedSignupForm;
