package com.livenessrn

import androidx.annotation.Nullable
import com.facebook.react.ReactPackage
import com.facebook.react.bridge.NativeModule
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.WritableMap
import com.facebook.react.modules.core.DeviceEventManagerModule.RCTDeviceEventEmitter
import com.facebook.react.uimanager.ViewManager


class LivenessRnPackage : ReactPackage {
  override fun createNativeModules(reactContext: ReactApplicationContext): List<NativeModule> {
    return listOf(LivenessRnModule(reactContext))
  }

  override fun createViewManagers(reactContext: ReactApplicationContext): List<ViewManager<*, *>> {
    return listOf(LivenessViewManager(reactContext))
  }

  companion object {
    fun sendEvent(
      context: ReactApplicationContext,
      eventName: String?,
      @Nullable params: WritableMap?
    ) {
      context
        ?.getJSModule(RCTDeviceEventEmitter::class.java)
        ?.emit(eventName!!, params)
    }
  }
}
