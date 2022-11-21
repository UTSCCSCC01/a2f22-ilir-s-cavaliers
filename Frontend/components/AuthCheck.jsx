import { useRouter } from "next/router";
import { useEffect } from "react";
import { useDispatch } from "react-redux";

import { validateCookie } from "../network";
import { displayErrorToast } from "../stores/ui/uiSlice";

/**
 * Authorization check, redirects if the user is not logged in
 */
export const AuthCheck = () => {
  const router = useRouter();
  const dispatch = useDispatch();
  useEffect(() => {
    if (!validateCookie()) {
      router.push("/");
      dispatch(
        displayErrorToast("An error has occurred", "Please log in.", true)
      );
    }
  }, []);
  return null;
};

export default AuthCheck;
