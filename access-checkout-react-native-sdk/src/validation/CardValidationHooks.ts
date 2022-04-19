import { useEffect } from 'react';
import { NativeEventEmitter } from 'react-native';
import AccessCheckoutReactNative, {
  AccessCheckout,
  CardValidationConfig,
  cardValidationNativeEventListenerOf,
} from '../index';
// eslint-disable-next-line  @typescript-eslint/ban-ts-comment
// @ts-ignore
import { CardValidationEventListener } from './CardValidationEventListener';

export function useCardValidationEventListener(
  merchantListener: CardValidationEventListener
) {
  useEffect(() => {
    const nativeEventListener =
      cardValidationNativeEventListenerOf(merchantListener);
    const nativeEventEmitter = new NativeEventEmitter(
      AccessCheckoutReactNative
    );

    const eventSubscription = nativeEventEmitter.addListener(
      AccessCheckout.CardValidationEventType,
      nativeEventListener
    );

    return () => {
      eventSubscription.remove();
    };
  }, []);
}

export function useCardValidation(
  accessCheckout: AccessCheckout,
  cardValidationConfig: CardValidationConfig,
  merchantListener: CardValidationEventListener
) {
  useCardValidationEventListener(merchantListener);

  const initialiseCardValidation = () => {
    return accessCheckout.initialiseCardValidation(cardValidationConfig);
  };

  return { initialiseCardValidation };
}
