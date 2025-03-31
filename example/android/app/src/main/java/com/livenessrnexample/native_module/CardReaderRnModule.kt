package com.livenessrnexample.native_module

import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod
import com.livenessrnexample.MainActivity

class CardReaderRnModule(reactContext: ReactApplicationContext) :
  ReactContextBaseJavaModule(reactContext) {

  override fun getName(): String {
    return NAME
  }

  companion object {
    const val NAME = "CardReaderRn"
  }

  override fun canOverrideExistingModule(): Boolean {
    return true
  }

  @ReactMethod
  fun readCard(appId: String, baseURL: String, publicKey: String, privateKey: String, promise: Promise) {
    val activity = (reactApplicationContext.currentActivity as MainActivity)
    activity.readCardPromise = promise
    activity.InitCardReaderSdk(appId, baseURL, publicKey, privateKey).execute()
    activity.doNextStep()
  }
}
