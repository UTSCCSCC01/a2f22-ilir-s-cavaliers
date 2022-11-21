import { useRouter } from "next/router";
import React, { useEffect } from "react";
import { useDispatch } from "react-redux";

import { deleteUserCookie } from "../../network";
import { displaySuccessToast } from "../../stores/ui/uiSlice";

const Logout = () => {
  /**
   * Hooks
   */
  const router = useRouter();
  const dispatch = useDispatch();

  // Deletes the cookie and redirects on load
  useEffect(() => {
    deleteUserCookie();
    dispatch(displaySuccessToast("Logout successful", "Have a good day!"));
    router.push("/");
  }, []);
  return (
    <>
      <div className="container">Logout</div>
    </>
  );
};

export default Logout;
