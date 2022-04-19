import { useEffect } from 'react';
import { NativeEventEmitter } from 'react-native';
import AccessCheckoutReactNative, { AccessCheckout, cardValidationNativeEventListenerOf } from '../index'; // eslint-disable-next-line  @typescript-eslint/ban-ts-comment
// @ts-ignore

export function useCardValidationEventListener(merchantListener) {
  useEffect(() => {
    const nativeEventListener = cardValidationNativeEventListenerOf(merchantListener);
    const nativeEventEmitter = new NativeEventEmitter(AccessCheckoutReactNative);
    const eventSubscription = nativeEventEmitter.addListener(AccessCheckout.CardValidationEventType, nativeEventListener);
    return () => {
      eventSubscription.remove();
    };
  }, []);
}
export function useCardValidation(accessCheckout, cardValidationConfig, merchantListener) {
  useCardValidationEventListener(merchantListener);

  const initialiseCardValidation = () => {
    return accessCheckout.initialiseCardValidation(cardValidationConfig);
  };

  return {
    initialiseCardValidation
  };
}
//# sourceMappingURL=CardValidationHooks.js.map