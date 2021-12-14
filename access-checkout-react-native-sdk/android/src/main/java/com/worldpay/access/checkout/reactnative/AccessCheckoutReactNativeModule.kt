package com.worldpay.access.checkout.reactnative

import android.os.Handler
import android.os.Looper
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.facebook.react.bridge.*
import com.facebook.react.uimanager.util.ReactFindViewUtil
import com.worldpay.access.checkout.client.session.AccessCheckoutClient
import com.worldpay.access.checkout.client.session.AccessCheckoutClientBuilder
import com.worldpay.access.checkout.client.session.model.CardDetails
import com.worldpay.access.checkout.client.validation.AccessCheckoutValidationInitialiser
import com.worldpay.access.checkout.client.validation.config.CardValidationConfig
import com.worldpay.access.checkout.reactnative.config.GenerateSessionConfigConverter
import com.worldpay.access.checkout.reactnative.config.ValidationConfigConverter
import java.lang.Exception

/**
 * Module class that implements all the functionality that is required by Javascript for the end user
 *
 * The responsibility of this class is the provide react methods that are then exposed for the JS to use.
 */
class AccessCheckoutReactNativeModule(private val reactContext: ReactApplicationContext) : ReactContextBaseJavaModule(reactContext) {

  private var accessCheckoutClient: AccessCheckoutClient? = null
  private val sessionResponseListener = SessionResponseListenerImpl()

  /**
   * Retrieves the name of the module that the JS is to refer to this by.
   *
   * Important: This must be the returning the same value as the module in ios
   *
   * In Javascript, this module can be referred by using the following:
   *
   * const { AccessCheckoutReactNative } = ReactNative.NativeModules;
   */
  override fun getName() = "AccessCheckoutReactNative"

  /**
   * Exposes the generateSession method to JS
   *
   * @ReactMethod annotation is needed as all native modules that need to be invoked must have this annotation
   *
   * @param readableMap [ReadableMap] represents the configuration object that the generate sessions function will use
   * @param promise [Promise] represents the JS promise that the corresponding JS method will return
   */
  @ReactMethod
  fun generateSessions(readableMap: ReadableMap, promise: Promise) {
    Handler(Looper.getMainLooper()).post {
      val config = GenerateSessionConfigConverter().fromReadableMap(readableMap)

      if (accessCheckoutClient == null) {
        accessCheckoutClient = AccessCheckoutClientBuilder()
                .baseUrl(config.baseUrl)
                .merchantId(config.merchantId)
                .sessionResponseListener(sessionResponseListener)
                .context(reactApplicationContext)
                .lifecycleOwner(getLifecycleOwner())
                .build()
      }

      sessionResponseListener.promise = promise

      val cardDetails = CardDetails.Builder()
              .pan(config.panValue)
              .expiryDate(config.expiryValue)
              .cvc(config.cvcValue)
              .build()

      accessCheckoutClient!!.generateSessions(cardDetails, config.sessionTypes)
    }
  }

  /**
   * Exposes the generateSession method to JS
   *
   * @ReactMethod annotation is needed as all native modules that need to be invoked must have this annotation
   *
   * @param readableMap [ReadableMap] represents the configuration object that the validation function will use
   * @param promise [Promise] represents the JS promise that the corresponding JS method will return
   */
  @ReactMethod
  fun initialiseValidation(readableMap: ReadableMap, promise: Promise) {
    try {
      val config = ValidationConfigConverter().fromReadableMap(readableMap)
      val rootView = reactContext.currentActivity?.window?.decorView?.rootView

      val panView = ReactFindViewUtil.findView(rootView, config.panId) as EditText
      val expiryView = ReactFindViewUtil.findView(rootView, config.expiryId) as EditText
      val cvcView = ReactFindViewUtil.findView(rootView, config.cvcId) as EditText

      val cardValidationConfigBuilder = CardValidationConfig.Builder()
              .baseUrl(config.baseUrl)
              .pan(panView)
              .expiryDate(expiryView)
              .cvc(cvcView)
              .validationListener(CardValidationListener(reactContext))
              .lifecycleOwner(getLifecycleOwner())
              .acceptedCardBrands(config.acceptedCardBrands)

      if (config.enablePanFormatting) {
        cardValidationConfigBuilder.enablePanFormatting()
      }

      Handler(Looper.getMainLooper()).post {
        try {
          AccessCheckoutValidationInitialiser.initialise(cardValidationConfigBuilder.build())
          promise.resolve(true)
        } catch (ex: Exception) {
          promise.reject(ex)
        }
      }
    } catch (ex : Exception) {
      promise.reject(ex)
    }
  }

  private fun getLifecycleOwner() = (reactContext.currentActivity as AppCompatActivity)
}