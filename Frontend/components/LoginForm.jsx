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
import { login, isLoginPendingSelector } from "../stores/users/userSlice";

//Validation schema for login
export const LoginSchema = Yup.object().shape({
  email: Yup.string().email().required(),
  password: Yup.string().required(),
});

// Basic Login Form Component
export function LoginForm({
  closeAllOverlays,
  errors,
  handleSubmit,
  setFieldValue,
  values: { email, password },
}) {
  const [show, setShow] = useState(false);
  const handleClick = () => setShow(!show);

  const isPending = useSelector(isLoginPendingSelector);

  return (
    <Form>
      <FormControl id="login-email">
        <FormLabel>Email</FormLabel>
        <Input
          value={email}
          onChange={(e) => setFieldValue("email", e.target.value)}
          placeholder="Enter email"
        />
      </FormControl>
      <FormHelperText style={{ color: "red" }}>
        {errors && errors["email"]}
      </FormHelperText>
      <FormControl id="login-password" mt={2}>
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

// Login form component with validation (see Formik guides)
export const EnhancedLoginForm = withFormik({
  enabledReinitialize: true,
  handleSubmit: ({ email, password }, { props: { login } }) => {
    login({ email, password });
  },
  mapPropsToValues: (props) => ({
    email: "",
    password: "",
  }),
  validationSchema: () => LoginSchema,
  validateOnBlur: false,
  validateOnChange: false,
})(LoginForm);

const ConnectedLoginForm = () => {
  const dispatch = useDispatch();

  return (
    <EnhancedLoginForm
      closeAllOverlays={() => dispatch(closeAllOverlays())}
      login={(args) => dispatch(login(args))}
    />
  );
};

export default ConnectedLoginForm;
