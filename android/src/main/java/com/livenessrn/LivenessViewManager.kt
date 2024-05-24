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
import com.liveness.sdk.core.model.LivenessModel
import com.liveness.sdk.core.model.LivenessRequest
import com.liveness.sdk.core.utils.CallbackLivenessListener
import java.util.UUID


class LivenessViewManager(
  private val reactContext: ReactApplicationContext
) : ViewGroupManager<LivenessView>() {
  private var requestId: String = ""
  private var appId: String = "com.qts.test"

  private var propWidth: Int? = null
  private var propHeight: Int? = null

  override fun getName() = REACT_CLASS

  override fun createViewInstance(reactContext: ThemedReactContext) =
    LivenessView(reactContext)

  override fun getCommandsMap() = mapOf("create" to COMMAND_CREATE)

  override fun getExportedCustomDirectEventTypeConstants(): Map<String, Any>? {
    return MapBuilder.builder<String, Any>()
      .put(
        "nativeClick",  //Same as name registered with receiveEvent
        MapBuilder.of("registrationName", "onEvent")
      )
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

    when (commandId.toInt()) {
      COMMAND_CREATE -> createFragment(root, reactNativeViewId)
    }
  }

  @ReactPropGroup(names = ["width", "height"], customType = "Style")
  fun setStyle(view: FrameLayout, index: Int, value: Int) {
    if (index == 0) propWidth = value
    if (index == 1) propHeight = value
  }

  @ReactProp(name = "requestid")
  fun setRequestid(view: FrameLayout, requestid: String) {
    this.requestId = requestid
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

    val activity = reactContext?.currentActivity as FragmentActivity

    LiveNessSDK.setLivenessRequest(activity, getLivenessRequest())

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

  }

  private var deviceId = ""
  private var secret = "ABCDEFGHIJKLMNOP"
  private var baseURL = "https://face-matching.vietplus.eu"

  private fun getLivenessRequest(): LivenessRequest {
    val privateKey = "-----BEGIN PRIVATE KEY-----\n" +
      "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQDChqQeJapdPhq6\n" +
      "oxRo2okcLdTLvAXXUCxUUaUeMjOHnzCBkpEuidqAYw/BbktH+aAhBE4ZlvptuP0M\n" +
      "iRRRFrd16ckjCFisWIwQsm0LwMPcreegsnzr8a00nsegqq3dFeNS4wxfXLEv7puu\n" +
      "UIz7h/8tPBtrTEl1r0adV4AwWfw+yIOX58wAVv3gNWMoSPVt3g27N+iSnHAXWQg3\n" +
      "EkZa9y13yiDPFbBVWsqWo3nIpy+5OkNaNNJ2tlKaxViCSYKo/bxBsEcdgu9l3LWL\n" +
      "Nw2y8OyDaVUk2j//L0re3Ic+tTqrFmpYCPvntfFlejLjk/TmV2Gh1Gq5K/ACyGWf\n" +
      "z36Wn1dbAgMBAAECggEAPOg7atUQSrGvXNDDCzGhzJjtD/2HNqt7tcr6kEHXsJc/\n" +
      "cNKbcp7nM1vRCxelqpuWb8ARVCHZt5E8ajhhjCI4v0q8F6P9X7k54eB8FDn/GZG5\n" +
      "/K7mPloliVxN1Rib56V6z/EXZqR3NjLXu/Ssr/UdCOTREP+J4LdIvOswz/Lc8Cr7\n" +
      "rq6EQ+GvQp1/mHuZjwe/8U4uj5pgKRtHwL1bV9Nj/tvGXqBzReEGlXPO6wXlQiDm\n" +
      "cfhlYQIp7HIujQqQ2bJHezKa4FwDgSUfFwvc0b/4UDqmKRcQcKlcI5f8C2z45rMt\n" +
      "SeWU3Wo4X0CwepH5K7Np4DInlJBLWmKf+W63KkZ/wQKBgQD5SxIV3pfKqeoQQfr8\n" +
      "Az5edTlxvIvBfWBe6JqotolCQ0zfvdnL6Sfc9m8Sb2m0ulmUuAgESUF5UxxG0zPp\n" +
      "A3ao4z9um+0TeqkOaokapYqVCt2g2fe5Ahn9mpXhaGW7NX1RGsyMG0ZKcMwzQnvT\n" +
      "/RUOsQIzbBtEPsABWuJL7E1JrwKBgQDHwmC0AYkRBm2hEnp2PWVPvy5Fwn3fKb9c\n" +
      "zkD4sx3T0pVXflXq7kqGvuoxqwUv15PTOeSniRVMeOm0k1MbPX3g/7Mj6/3Ox6nR\n" +
      "sUKcWf2FR2eYTOpkjyzlTs3J0wEY0F6psZKvCaDIq+iWZOGWnECOx1oNssSe24Mf\n" +
      "WmanOat0FQKBgQCzhOXfLc4tOTK+xmTQ+hz9tHjLeLVDft/ZOLO27svlFcXUEUk3\n" +
      "2AzGyAewRN7gMJm1yNitDXvCvKDON+VX1RCsCglxw4Nz1Y7MPNfCgpdLQOncnVoQ\n" +
      "tny07Y8lJSKtL5WwxBWshy+VB6lk+GlnfQae78Tf9ueju3RVBXeUqPP3LQKBgDRM\n" +
      "WZEHDeGdMzqGYfVu4YVdYjn1T4sLnQQpI615B4gbi+naM5hMRsq8VQgn5DXAVP1q\n" +
      "HMkYBLF+voD+STXKhZhQmZAACvUGJRm3NN9GWC96oE6pZHxrV6+5T/tU+OaMNxxq\n" +
      "VyLofGXNTBmD0+PgiK5Y1uTbNsr4YX3l5xZE7PR5AoGAY02BlFRX8+x+B8H2EEqu\n" +
      "pDEyFytnXQ25ZeBdkywuj95yX5jlkzFZCsvqVUVmRPglwkR5fR2dWtNprhRgezwL\n" +
      "9pOFz3bQ1DN3EBY3nO/85aro/mE/nsnnQw4Vj38dtgPRuX60slCxHcQAv9PUJYBz\n" +
      "Hci6AX3smA2ulUvchtH6uPQ=\n" +
      "-----END PRIVATE KEY-----\n"
    val public_key = "-----BEGIN CERTIFICATE-----\n" +
      "MIIDjzCCAnegAwIBAgIEPhgWFTANBgkqhkiG9w0BAQsFADBbMScwJQYDVQQDDB5SZWdlcnkgU2Vs\n" +
      "Zi1TaWduZWQgQ2VydGlmaWNhdGUxIzAhBgNVBAoMGlJlZ2VyeSwgaHR0cHM6Ly9yZWdlcnkuY29t\n" +
      "MQswCQYDVQQGEwJVQTAgFw0yNDA0MTEwMDAwMDBaGA8yMTI0MDQxMTAzMTMwOVowUTEdMBsGA1UE\n" +
      "AwwUcXVhbmd0cnVuZ3F0cy5jb20udm4xIzAhBgNVBAoMGlJlZ2VyeSwgaHR0cHM6Ly9yZWdlcnku\n" +
      "Y29tMQswCQYDVQQGEwJVQTCCASIwDQYJKoZIhvcNAQEBBQADggEPADCCAQoCggEBAIfK7LjzjqCo\n" +
      "VSzXz/ROHc2IyBMc89GwnR0slF1Lenavs+r+lnjFAxkVonBRTjtMj1pWqlACnd3qiIAD/8GbSagG\n" +
      "qsV43BDPbioDibWg/9wln82VLwEQohjLTl7VJtKuRAIUcg2nY4r5LNzpdClJx+k7zrIVDKSO8tRa\n" +
      "onU1dU6KLSmC2ZOzT10zrK4qmjvN/LFp0rlXJtdw++MUOIM9kccyi+3MK7iiraNV7Tlazy9xF0OZ\n" +
      "ytzgSX5R+oHE3aUS0M+W4p/dhihvLKjiejuw46E0dqEKxaqMJHXj2Qei1Ky1RrdRBNB0oQLCoUGx\n" +
      "KRaYw1CbZ7QWAgnrbqTvs1Y8pwUCAwEAAaNjMGEwDwYDVR0TAQH/BAUwAwEB/zAOBgNVHQ8BAf8E\n" +
      "BAMCAYYwHQYDVR0OBBYEFIlsqZHH0jmPvIjlF4YARXnamm7AMB8GA1UdIwQYMBaAFIlsqZHH0jmP\n" +
      "vIjlF4YARXnamm7AMA0GCSqGSIb3DQEBCwUAA4IBAQBfSk1XtHU8ix87g+lVzRQrEf7qsqWiwkN9\n" +
      "TW05qaPDMoMEoe/MW0YJZ+QwgvGMNLkEWjz/v0p1fVFF6kIolbo1o+1P6D4RCWvyB8S5zV9Mv+aR\n" +
      "1uWbAYiAA2uql/NrIJ3V1pJhIgRgDsRNuVP8MhNZc6DgJQLZOMKLwXsNHDtGOHk+ZcPiyWcjb4a3\n" +
      "voZCp4HN8+V2umO+QGuESZhTLihBnXv9HTpKxwWu4tK/4dgngDYM3UmChRjD/H7A3aYV4Xyxkqw2\n" +
      "rnd2LAr/zUEhFkbs21iG3DF0cHGKI15YzIq5pEhb9l4ePcCIgWgnJDNJPA/QhxpRB1XhP4bpK8kP\n" +
      "GJ8f\n" +
      "-----END CERTIFICATE-----\n"
    if (deviceId.isNullOrEmpty()) {
      val activity = reactContext?.currentActivity as FragmentActivity
      deviceId = LiveNessSDK.getDeviceId(activity)!!
    }

    val optionRequest: HashMap<String, String> = HashMap()
    optionRequest["requestId"] = this.requestId
    optionRequest["clientTransactionId"] = this.requestId
    return LivenessRequest(
      duration = 600, privateKey = privateKey,
      appId = this.appId,
      deviceId = deviceId, clientTransactionId = this.requestId, secret = secret,
      baseURL = baseURL, publicKey = public_key
    )
  }
}
