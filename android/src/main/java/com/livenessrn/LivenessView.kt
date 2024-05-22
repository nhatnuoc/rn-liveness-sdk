package com.livenessrn

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.widget.FrameLayout
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.ReactContext
import com.facebook.react.uimanager.ThemedReactContext
import com.facebook.react.uimanager.UIManagerHelper
import com.facebook.react.uimanager.events.Event
import com.facebook.react.uimanager.events.RCTEventEmitter
import com.liveness.sdk.core.LiveNessSDK
import com.liveness.sdk.core.model.LivenessModel
import com.liveness.sdk.core.utils.CallbackLivenessListener
import com.livenessrn.events.RNLivenessEvent


class LivenessView @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {
  private val themedReactContext: ThemedReactContext = context as ThemedReactContext
  private val TAG = "LivenessView"
  private val EVENT_NAME = "onEvent"

  private val callBack = object : CallbackLivenessListener {
    override fun onCallbackLiveness(data: LivenessModel?) {
      Log.d("callBack", "data==== $data")
      val map = Arguments.createMap()

//      LivenessRnPackage.sendEvent(context as ReactApplicationContext,"onEvent", map)
      callBackEvent(data)
    }
  }

  init {
    Log.d("callBack", "data==== 1122122112")
    LiveNessSDK.setCallbackListener(callBack)
    emitEvent("A===========================")
  }

  private fun callBackEvent(data: LivenessModel?) {
    val reactContext = context as ReactContext
    reactContext.getJSModule(RCTEventEmitter::class.java)
      .receiveEvent(id, LivenessViewManager.EVENT_NAME, Arguments.createMap().apply {
        putString("text", data?.status?.toString())
      })
  }

  private fun emitEvent(data: String?) {
    val reactContext = context as ReactContext
    val id = id
    val dispatcher = UIManagerHelper.getEventDispatcherForReactTag(reactContext, id)
    val surfaceId = UIManagerHelper.getSurfaceId(reactContext)
    if (dispatcher != null) {
      dispatcher.dispatchEvent(RNLivenessEvent(surfaceId = surfaceId, id, 112))
    }
  }

//    val reactContext = context as ReactContext
//    reactContext.getJSModule(RCTEventEmitter::class.java)
//      .receiveEvent(id, LivenessViewManager.EVENT_NAME, Arguments.createMap().apply {
//        putString("text", data)
//      })
//  }
}
