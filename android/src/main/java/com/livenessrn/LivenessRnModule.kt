package com.livenessrn

import android.util.Log
import androidx.fragment.app.FragmentActivity
import com.facebook.react.bridge.Callback
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod
import com.facebook.react.bridge.WritableMap
import com.facebook.react.bridge.WritableNativeMap
import com.liveness.sdk.core.LiveNessSDK
import com.liveness.sdk.core.model.LivenessModel
import com.liveness.sdk.core.model.LivenessRequest
import com.liveness.sdk.core.utils.CallbackLivenessListener
import java.util.UUID


class LivenessRnModule(reactContext: ReactApplicationContext) :
  ReactContextBaseJavaModule(reactContext) {

  override fun getName(): String {
    return NAME
  }

  companion object {
    const val NAME = "LivenessRn"
  }

  private var deviceId = ""
  private var appId = ""
  private var secret = "ABCDEFGHIJKLMNOP"
  private var baseURL = "https://face-matching.vietplus.eu"
  private var clientTransactionId = "TEST"

  @ReactMethod
  fun configure(appId: String, secret: String? = null, baseURL: String? = null, clientTransactionId: String? = null) {
    this.appId = appId
    if (secret != null && secret != "") {
      this.secret = secret
    }
    if (baseURL != null && baseURL != "") {
      this.baseURL = baseURL
    }
    if (clientTransactionId != null && clientTransactionId != "") {
      this.clientTransactionId = clientTransactionId
    }
  }

  @ReactMethod
  fun getDeviceId(callback: Callback? = null) {
    currentActivity!!.runOnUiThread {
      val mDeviceId = LiveNessSDK.getDeviceId(reactApplicationContext.currentActivity as FragmentActivity)
      val resultData: WritableMap = WritableNativeMap()
      if (mDeviceId?.isNotEmpty() == true) {
        deviceId = mDeviceId
        resultData.putString("deviceId", "$mDeviceId")
        callback?.invoke(resultData)
      } else {
        resultData.putString("deviceId", "empty")
        callback?.invoke(resultData)
      }
    }
  }

  @ReactMethod
  fun registerFace(image: String? = null, callback: Callback? = null) {
    currentActivity!!.runOnUiThread {
      LiveNessSDK.registerFace(
        reactApplicationContext.currentActivity as FragmentActivity,
        getLivenessRequest(image),
        object : CallbackLivenessListener {
          override fun onCallbackLiveness(data: LivenessModel?) {
            val resultData: WritableMap = WritableNativeMap()
            resultData.putInt("status", data?.status ?: -1)
            resultData.putString("data", "${data?.data ?: ""}")
            resultData.putString("message", "${data?.message ?: ""}")
            resultData.putString("code", "${data?.code ?: ""}")
            resultData.putString("pathVideo", "${data?.pathVideo ?: ""}")
            resultData.putString("faceImage", "${data?.faceImage ?: ""}")
            resultData.putString("livenessImage", "${data?.livenessImage ?: ""}")
            callback?.invoke(resultData)
          }
        })
    }
  }

  @ReactMethod
  fun startLiveNess(callback: Callback? = null) {
//    currentActivity!!.runOnUiThread {
//      LiveNessSDK.startLiveNess(
//        reactApplicationContext.currentActivity as FragmentActivity,
//        getLivenessRequest(),
//        object : CallbackLivenessListener {
//          override fun onCallbackLiveness(data: LivenessModel?) {
//            Log.d("CALLBACK DATA", "$data")
//            val resultData: WritableMap = WritableNativeMap()
//            resultData.putInt("status", data?.status ?: -1)
//            resultData.putString("data", "${data?.data ?: ""}")
//            resultData.putString("message", "${data?.message ?: ""}")
//            resultData.putString("code", "${data?.code ?: ""}")
//            resultData.putString("pathVideo", "${data?.pathVideo ?: ""}")
//            resultData.putString("faceImage", "${data?.faceImage ?: ""}")
//            resultData.putString("livenessImage", "${data?.livenessImage ?: ""}")
//            callback?.invoke(resultData)
//            callback?.invoke(resultData)
//          }
//        })
//    }
  }

  private fun getLivenessRequest(image: String? = null): LivenessRequest {
    Log.d("checkLiveNessFlash", "checkLiveNessFlash==== 3= $image")
    val privateKey = "-----BEGIN PRIVATE KEY-----\n" +
      "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQDAxJOlDUyio4nz\n" +
      "JzykUPihuBnpume0Y5Fzo0bj6Xi6DD/Jjp5r8RxcgdqLUuNUe0vHqmMqgP1t6ZK0\n" +
      "0+lDRrjaSk8b7xjv9L7dm986SyZEs8Bgtqel4Tw3DkHj2i1c13zeUj6OgKPrYBqd\n" +
      "28EDSLQw+Cf8gYxiY2VMoEUDZ7cV0jrT6U/VDI4aDl9b8XrVUwtbAuN8t86gZVrY\n" +
      "gpIG9HLnEQZXJKFG2jXFktrDgm1wlfN5ve/qBRhMHimIDwCgsYT7ll/I7eaMEhLF\n" +
      "/+nylvVWbpPvLMXA8E9EBbCTkLmh5XMXK6N0uxidJ8GHeAF0lP1YMT4ctDd1sfxI\n" +
      "C0a56m/tAgMBAAECggEAIHv/9Xx8QZwVjyg5i+cpPvdrRnvnJfrxc+877wRVF+Ug\n" +
      "irLB96a2BNDNJ1VcgcwVRRxtgagjDPajhl1/nZq5Y+/JzQdJyIYR8/5ka8pmNIWm\n" +
      "EaY2Mjn2j8CTUfZeVprpq/1oFVOQTAXx9lAmAoup8eaftmmOYrYlR/hqKNy9eauc\n" +
      "2DiHOpO0Eh1TkU8pydte3xbUzyurJz1o+pvg9mRMeaGLrEnocu2mW9oy488TdNIO\n" +
      "oGeOOW4AV/XYzv4TtXWqUM6HcSVA3IcZAFBdi6IeQYKuObrfd9iphONcpklb+PFV\n" +
      "5XRmEKEQyhjd19AucVbGERhe+3l2NyiL13T6RLJxcQKBgQDnIAzvppgFRNNnROYV\n" +
      "jY8WfU7g7lQ0syh6e3ANIc5vSqRspwJiPK6u+PaoUtjqvVoIjig9owDVK+yy7nR4\n" +
      "fPoUxZjCX0x+N5KE+UEmH/WaKl+twpYkVIjD43FFEX0448qqkKJ5uwhkHEXu9uwH\n" +
      "q1dUOh4YlL85BIErkmdMv+e6RwKBgQDVg7UTPpz95WyJCwU0TO5QC6cfzzB1a5ol\n" +
      "z3gOt5f+SACNINg9Er6M+gewTq5ZWXIr8+EET9HfxvIeICqOXMzcotMM9l0Gv1AL\n" +
      "uitjw82KMJRsDWi01OWcrhLC/2MY18H8A2Bgl4Eo4vEHrW2yWz8obNSPDNCECY7z\n" +
      "BBAMS92qKwKBgHJCbwK+2iKortY7wn9fNyDIHAmo6OoQs+8xPOjREKwGO5kXS19j\n" +
      "XnxTyiTWqDQMHglitdQa1FuOVnry1ZOHPV6tfOKCmF9Be+bPDn0ZiaKIVjqhmvYk\n" +
      "8GPe+e1KQxyvyE5gKGKDqxdKvuvvGCqGcyrJfH1sc8htSKpQu06/BIEbAoGBAIyD\n" +
      "oxVJLZRB+k2uPPyQbH2tTY03k1KTP90GTqKQ3KxxwjselHCM925b1deH0GHo5aRb\n" +
      "WYi25w34JbsBvD/4frHtTivHrq0UFp/BI3ECmTAKjIMXyALJ4VpdjOdbn2HoDDfI\n" +
      "GRM4Yb4ArFM7JWgteMEn7jM+YbOjfrobwFv3SagFAoGBAImAsluYshbRa/IfVIJy\n" +
      "FgtmWmX/tWXRIRcLB/Y8GhreHg/U/dJPk4tXJChNixNYAL9c9EIrsUw3n20nrutv\n" +
      "qJXxXrtZdIF5Rl7HiuKtnMheYwmbYEz6hAfbO04px13RbjjS3kcGofOPa3u0q8s2\n" +
      "6DNOTXIFuW/9FIrD32skm7hY\n" +
      "-----END PRIVATE KEY-----"
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
      deviceId = UUID.randomUUID().toString()
    }
    return LivenessRequest(
      duration = 600, privateKey = privateKey,
      appId = appId,
      imageFace = image,
      deviceId = deviceId, clientTransactionId = clientTransactionId, secret = secret,
      baseURL = baseURL, publicKey = public_key
    )
  }
}
