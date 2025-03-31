package com.livenessrn

import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Choreographer
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.FrameLayout.LayoutParams
import androidx.fragment.app.FragmentActivity
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReadableArray
import com.facebook.react.common.MapBuilder
import com.facebook.react.uimanager.ThemedReactContext
import com.facebook.react.uimanager.ViewGroupManager
import com.facebook.react.uimanager.annotations.ReactProp
import com.facebook.react.uimanager.annotations.ReactPropGroup
import com.liveness.sdk.corev4.LiveNessSDK
import com.liveness.sdk.corev4.model.DataConfig
import com.liveness.sdk.corev4.model.LivenessRequest
import com.liveness.sdk.corev4.model.VerifyLevel
import java.util.Random

class LivenessViewManager(
  private val reactContext: ReactApplicationContext
) : ViewGroupManager<LivenessView>() {

  private var idCardRequestId: String = ""
  private var appId: String = "com.qts.test"
  private var deviceId = ""
  private var baseURL = ""
  private var privateKey = ""
  private var publicKey = ""
  private var debugging: Boolean = false
  private var useFlash: Boolean = true
  private var offline: Boolean = false

  private var propWidth: Int? = null
  private var propHeight: Int? = null
  private var id: Int = -1;

  override fun getName() = REACT_CLASS

  override fun createViewInstance(reactContext: ThemedReactContext): LivenessView {
    return LivenessView(reactContext).apply {
      id = View.generateViewId()
    }
  }

  override fun getCommandsMap() = mapOf("create" to COMMAND_CREATE)

  override fun onDropViewInstance(view: LivenessView) {
    super.onDropViewInstance(view)
    try {
      val activity = reactContext.currentActivity as FragmentActivity
      val fragmentManager = activity.supportFragmentManager
      if (fragmentManager.fragments.isNotEmpty()) {
        Log.d("createFragment", "remove fragment liveness")
        fragmentManager.fragments.forEach { fragment ->
          Log.d("remove fragment liveness", "${fragment.id}")
          if (id == fragment.id) {
            fragmentManager.beginTransaction().remove(fragment).commitAllowingStateLoss()
            setBrightness(originalBrightness ?: 0.3f)
          }
        }
      }
    } catch (_: Exception) {}
  }

  fun setBrightness(value: Float) {
    val window = (reactContext.currentActivity as FragmentActivity).window
    val handler = Handler(Looper.getMainLooper())
    handler.post {
      if (window != null) {
          window.attributes = window.attributes.apply {
              screenBrightness = value
          }
      }
    }
  }

  fun getBrightness() {
    val window = (reactContext.currentActivity as FragmentActivity).window
    if (originalBrightness == null && window != null) {
        originalBrightness = window.attributes.screenBrightness
    }
  }

  override fun getExportedCustomDirectEventTypeConstants(): Map<String, Any> {
    return MapBuilder.builder<String, Any>()
      .put("onSuccess", MapBuilder.of("registrationName", "onSuccess"))
      .build()
  }

  /**
   * Handle "create" command (called from JS) and call createFragment method
   */
  override fun receiveCommand(
    root: LivenessView,
    commandId: String,
    args: ReadableArray?
  ) {
    super.receiveCommand(root, commandId, args)
    val reactNativeViewId = requireNotNull(args).getInt(0)
    id = reactNativeViewId
    when (commandId.toInt()) {
      COMMAND_CREATE -> createFragment(root, reactNativeViewId)
    }
  }

  @ReactPropGroup(names = ["width", "height"], customType = "Style")
  fun setStyle(view: FrameLayout, index: Int, value: Int) {
    if (index == 0) propWidth = value
    if (index == 1) propHeight = value
  }

  @ReactProp(name = "idCardRequestId")
  fun setIdCardRequestId(view: FrameLayout, idCardRequestId: String) {
    this.idCardRequestId = idCardRequestId
    this.setupLiveness(view)
  }

  @ReactProp(name = "appId")
  fun setAppId(view: FrameLayout, appId: String) {
    this.appId = appId
    this.setupLiveness(view)
  }

  @ReactProp(name = "baseURL")
  fun setBaseUrl(view: FrameLayout, baseUrl: String) {
    this.baseURL = baseUrl
    this.setupLiveness(view)
  }

  @ReactProp(name = "privateKey")
  fun setPrivateKey(view: FrameLayout, privateKey: String) {
    this.privateKey = privateKey
    this.setupLiveness(view)
  }

  @ReactProp(name = "publicKey")
  fun setPublicKey(view: FrameLayout, publicKey: String) {
    this.publicKey = publicKey
    this.setupLiveness(view)
  }

  @ReactProp(name = "debugging")
  fun setDebugging(view: FrameLayout, debugging: Boolean) {
    this.debugging = debugging
    this.setupLiveness(view)
  }

  @ReactProp(name = "useFlash")
  fun setUseFlash(view: FrameLayout, useFlash: Boolean) {
    this.useFlash = useFlash
    this.setupLiveness(view)
  }

  @ReactProp(name = "offline", defaultBoolean = false)
  fun setOffline(view: FrameLayout, offline: Boolean) {
    this.offline = offline
    this.setupLiveness(view)
  }

  private val handler = Handler(Looper.getMainLooper())
  private var isPendingCheck = false

  private fun setupLiveness(view: FrameLayout) {
    if (isPendingCheck) return
    isPendingCheck = true

    handler.postDelayed({
      isPendingCheck = false
      if (offline || (!appId.isNullOrEmpty() && !baseURL.isNullOrEmpty() &&
          !publicKey.isNullOrEmpty() && !privateKey.isNullOrEmpty())) {
        startLiveness(view)
      }
    }, 100)
  }

  private fun startLiveness(view: FrameLayout) {
    val activity = reactContext.currentActivity as? FragmentActivity ?: return
    val fragmentManager = activity.supportFragmentManager
    val containerId = view.id // Lấy ID của FrameLayout

    if (offline) {
      LiveNessSDK.startLiveNess(activity, getLivenessRequest(), fragmentManager, containerId, null, false)
      return
    }

    if (appId.isNullOrEmpty() || baseURL.isNullOrEmpty() || publicKey.isNullOrEmpty() || privateKey.isNullOrEmpty()) {
      return
    }
    createFragment(view, containerId)
    LiveNessSDK.startLiveNess(activity, getLivenessRequest(), fragmentManager, containerId, null, false)
  }

  /**
   * Replace your React Native view with a custom fragment
   */
  private fun createFragment(root: FrameLayout, reactNativeViewId: Int) {
    val parentView = root.findViewById<ViewGroup>(reactNativeViewId)
    setupLayout(parentView)
    Log.d("createFragment", "Start liveness: $reactNativeViewId")
  }

  private fun setupLayout(view: View) {
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
    var originalBrightness: Float? = null
  }

  private fun getLivenessRequest(): LivenessRequest {
    val activity = reactContext.currentActivity as FragmentActivity;
    if (LiveNessSDK.getDeviceId(activity)?.isNotEmpty() == true) {
      deviceId = LiveNessSDK.getDeviceId(activity)!!
    }

    val optionRequest: HashMap<String, String> = HashMap()
//    optionRequest["requestId"] = this.requestId
//    optionRequest["clientTransactionId"] = this.requestId

    val request = LivenessRequest(
      duration = 600,
      privateKey = privateKey,
      appId = this.appId,
      deviceId = deviceId,
      clientTransactionId = this.idCardRequestId,
//      secret = secret,
      baseURL = baseURL,
      publicKey = publicKey,
      isDebug = debugging,
      offlineMode = this.offline,
      isSaveImage = false,
      verifyLevel = VerifyLevel.LOW,
    )
//    request.colorConfig = listOf(0xFFFFFF00L, 0xFF800080L, 0xFFFFA500L)
     if (request.offlineMode) {
        val random = Random()
        request.dataConfig = DataConfig(
          random.nextInt(4),
          random.nextInt(4) + 1,
          quality = 70,
          maxWidth = 480,
        )
     }
    return request
  }
}
