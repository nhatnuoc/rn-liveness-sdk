package com.livenessrnexample.native_module

import com.facebook.react.ReactPackage
import com.facebook.react.bridge.NativeModule
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.WritableMap
import com.facebook.react.modules.core.DeviceEventManagerModule
import com.facebook.react.uimanager.ViewManager

class CardReaderRnPackage : ReactPackage {
  override fun createNativeModules(reactContext: ReactApplicationContext): List<NativeModule> {
    return listOf(CardReaderRnModule(reactContext))
  }

  override fun createViewManagers(reactContext: ReactApplicationContext): List<ViewManager<*, *>> {
//    return listOf(LivenessViewManager(reactContext))
    return emptyList()
  }

  companion object {
    fun sendEvent(
      context: ReactApplicationContext,
      eventName: String?,
      params: WritableMap?
    ) {
//      context
//        ?.getJSModule(RCTDeviceEventEmitter::class.java)
//        ?.emit(eventName!!, params)
      context
        .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter::class.java)
        ?.emit(eventName!!, params)
    }
  }
}
