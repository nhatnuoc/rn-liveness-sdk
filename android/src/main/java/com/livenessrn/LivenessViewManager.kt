package com.livenessrn

import android.os.Bundle
import android.util.Log
import android.view.Choreographer
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.FrameLayout.LayoutParams
import androidx.fragment.app.FragmentActivity
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.Callback
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReadableArray
import com.facebook.react.common.MapBuilder
import com.facebook.react.uimanager.ThemedReactContext
import com.facebook.react.uimanager.ViewGroupManager
import com.facebook.react.uimanager.annotations.ReactProp
import com.facebook.react.uimanager.annotations.ReactPropGroup
import com.liveness.sdk.core.LiveNessSDK
import com.liveness.sdk.core.MainLiveNessActivity
import com.liveness.sdk.core.model.DataModel
import com.liveness.sdk.core.model.LivenessModel
import com.liveness.sdk.core.model.LivenessRequest
import com.liveness.sdk.core.utils.CallbackLivenessListener


class LivenessViewManager(
  private val reactContext: ReactApplicationContext
) : ViewGroupManager<FrameLayout>() {
  private var requestId: String = ""
  private var appId: String = "com.qts.test"
  private var callback: Callback? = null

  private var propWidth: Int? = null
  private var propHeight: Int? = null

  private val callBack = object : CallbackLivenessListener{
    override fun onCallbackLiveness(livenessModel: LivenessModel?) {
      if (livenessModel != null && livenessModel.status != null && livenessModel.status == 200) {
        val map = Arguments.createMap()
        map.putInt("status", livenessModel.status ?: -1)
        map.putString("message", livenessModel.message ?: "")
        map.putString("request_id", livenessModel.requestId ?: "")
        map.putString("code", livenessModel.code ?: "")
        map.putBoolean("success", livenessModel.success ?: false)
        map.putString("pathVideo", livenessModel.pathVideo ?: "")
        map.putString("faceImage", livenessModel.faceImage ?: "")
        map.putString("livenessImage", livenessModel.livenessImage ?: "")
        map.putString("transactionID", livenessModel.transactionID ?: "")

        if (livenessModel.data != null) {
          val mapData = Arguments.createMap()
          map.putString("faceMatchingScore", livenessModel.data?.faceMatchingScore ?: "")
          map.putString("livenessType", livenessModel.data?.livenessType ?: "")
          map.putDouble("livenesScore", (livenessModel.data?.livenesScore ?: 0).toDouble())
          map.putMap("data", mapData)
        }
        LivenessRnPackage.sendEvent(reactContext,"onEvent", map)
      } else {
        val map = Arguments.createMap()
        map.putInt("status", livenessModel?.status ?: -1)
        map.putString("message", livenessModel?.message ?: "")
        map.putString("code", livenessModel?.code ?: "")
        LivenessRnPackage.sendEvent(reactContext,"onEvent", map)
      }
    }
  }

  override fun getName() = REACT_CLASS

  override fun createViewInstance(reactContext: ThemedReactContext) =
    LivenessView(reactContext)

  override fun getCommandsMap() = mapOf("create" to COMMAND_CREATE)

  override fun getExportedCustomBubblingEventTypeConstants(): Map<String?, Any?>? {
    return createExportedCustomDirectEventTypeConstants()
  }

  fun createExportedCustomDirectEventTypeConstants(): Map<String?, Any?>? {
    return MapBuilder.builder<String?, Any?>()
      .put(EVENT_NAME, MapBuilder.of("registrationName", EVENT_NAME)).build()
  }

  /**
   * Handle "create" command (called from JS) and call createFragment method
   */
  override fun receiveCommand(
    root: FrameLayout,
    commandId: String,
    args: ReadableArray?
  ) {
    super.receiveCommand(root, commandId, args)
    val reactNativeViewId = requireNotNull(args).getInt(0)

    when (commandId.toInt()) {
      COMMAND_CREATE -> createFragment(root, reactNativeViewId)
    }
  }

  @ReactPropGroup(names = ["width", "height"], customType = "Style")
  fun setStyle(view: FrameLayout, index: Int, value: Int) {
    if (index == 0) propWidth = value
    if (index == 1) propHeight = value
  }

  @ReactProp(name = "requestId")
  fun setRequestId(view: FrameLayout, requestId: String) {
    this.requestId = requestId
  }

  @ReactProp(name = "appId")
  fun setAppId(view: FrameLayout, appId: String) {
    this.appId = appId
  }

  /**
   * Replace your React Native view with a custom fragment
   */
  fun createFragment(root: FrameLayout, reactNativeViewId: Int) {
    val parentView = root.findViewById<ViewGroup>(reactNativeViewId)
    setupLayout(parentView)

    val bundle = Bundle()
    bundle.putString("KEY_BUNDLE_SCREEN", "")
    val myFragment = MainLiveNessActivity()
    myFragment.arguments = bundle

    LiveNessSDK.setCallbackListener(callBack)
    LiveNessSDK.setLivenessRequest(LivenessRequest(clientTransactionId = this.requestId))

    val activity = reactContext?.currentActivity as FragmentActivity
    activity.supportFragmentManager
      .beginTransaction()
      .replace(reactNativeViewId, myFragment, reactNativeViewId.toString())
      .commit()
  }

  fun setupLayout(view: View) {
    Choreographer.getInstance().postFrameCallback(object: Choreographer.FrameCallback {
      override fun doFrame(frameTimeNanos: Long) {
        manuallyLayoutChildren(view)
        view.viewTreeObserver.dispatchOnGlobalLayout()
        Choreographer.getInstance().postFrameCallback(this)
      }
    })
  }

  /**
   * Layout all children properly
   */
  private fun manuallyLayoutChildren(view: View) {
    // propWidth and propHeight coming from react-native props
    if (propWidth != null && propHeight != null) {
      val width = requireNotNull(propWidth)
      val height = requireNotNull(propHeight)

      view.measure(
        View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY),
        View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY))

      view.layout(0, 0, width, height)
    } else {
      view.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
    }
  }

  companion object {
    private const val REACT_CLASS = "LivenessViewManager"
    private const val COMMAND_CREATE = 1
    const val EVENT_NAME = "onTextChanged"

  }


}
