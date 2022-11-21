import { useToast } from "@chakra-ui/react";
import { useEffect, useState } from "react";
import { useSelector } from "react-redux";

import { toastsSelector } from "../stores/ui/selectors";

/**
 * Controller to display and interact with the toast messages
 */
export const ToastController = () => {
  const toastController = useToast();
  const [displayedToasts, setDisplayedToasts] = useState({});

  const toasts = useSelector(toastsSelector);

  useEffect(() => {
    if (toasts.length === 0) {
      toastController.closeAll();
    }

    toasts.forEach((toast) => {
      if (displayedToasts[toast.key]) return;
      displayedToasts[toast.key] = toast;
      setDisplayedToasts(displayedToasts);
      toastController({
        ...toast,
        isClosable: true,
        position: "bottom-left",
      });
    });
  }, [toasts, displayedToasts]);
  return null;
};

export default ToastController;
