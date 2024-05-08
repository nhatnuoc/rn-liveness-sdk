package com.livenessrn

import androidx.fragment.app.FragmentActivity
import com.facebook.react.bridge.Callback
import com.facebook.react.bridge.Promise
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
        getLivenessRequest(),
        object : CallbackLivenessListener {
          override fun onCallbackLiveness(data: LivenessModel?) {
            callback?.invoke(data)
          }
        })
    }
  }

  @ReactMethod
  fun startLiveNess(callback: Callback? = null) {
    currentActivity!!.runOnUiThread {
      LiveNessSDK.startLiveNess(
        reactApplicationContext.currentActivity as FragmentActivity,
        getLivenessRequest(),
        object : CallbackLivenessListener {
          override fun onCallbackLiveness(data: LivenessModel?) {
            callback?.invoke(data)
          }
        })
    }
  }

  private fun getLivenessRequest(): LivenessRequest {
    val privateKey = "-----BEGIN PRIVATE KEY-----\n" +
      "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQC7K1QJ6YLRMoNv\n" +
      "mqV3qkNXJZWxCT95+/xVlKhea/i3QpJk5Ier4hgUqn3MmlU3NXRv6eHZh579uzqM\n" +
      "1U8PJzMGj2j9XnlOkQtWImSKiMUPQYJ4CSnUGCLpiMomZKjGO4tLiS/yCSJ/BFrI\n" +
      "TBGYJhJL9wOYYzZdY/mNWSr1+ZsqQFH0Ug7n4XfYrJD7XgtozIgiY5LtbdoV/6At\n" +
      "TN8H8czzQnLaA8NjDb8npP7ogu99KVivtoAJJhLZJ9nvXyP9Dp7sKi9d33LaxFwH\n" +
      "OiNWRm3qFGm1G8RZoWg4G1h+ETL0z4COBgvAhToGI5gkkeyw+qFfS9hqLN0hJWAm\n" +
      "o41oGPo1AgMBAAECggEBAI1qzmNy4JmJjg+MDAufRKQazMBnmWNkhiJvYMt+zvxA\n" +
      "O3YpyWyQNtueedBWp55AMErCrxd5xiI2DaYNIV/0oTQKtSwC7qrzIlqhP9AASMwf\n" +
      "FiH14nnTBsXmyb46fd7RbIzVCbnZNww7URBXkU+hLF/jMf84rwHfINWwkqopPxir\n" +
      "F5Ohqt1G/PxzI3/rc20DzDJX331em5qHBqACp1JcHXtpaFKBOJihVnhYqxon9k1o\n" +
      "qcR79HNRlIwHWsxsOUEM8zPTbstQaqMgKLFXyENM43C+B/f+Oz2DBdF32RD7jq8Q\n" +
      "xLR1gidq+KCXEejOBuRexrrT4fQiCb7e2robh8o/IUECgYEA4XubVcqjmZmhIlt9\n" +
      "PU+63IC1dEVc40PZtJ5AiQvZa+zLCl9ik+9k/dmJE5WUZDki76W3OB+kJq3fUWgQ\n" +
      "tyo0UkpxHwqryefGg09syu5cNGE/zd7ZREF0aIsHnXaPtroKq8Z6mz4FctLt9egr\n" +
      "8V0M670N9rQz996+E/KHf4jEeBECgYEA1IBBZNWJDE7lIip8CobnPVh718p2HTuc\n" +
      "lxeTFrRgI3wWnitYhCGLnJMGDvNv/znApsB7aAgVFz3r5jGKxTPPCwa9gwrKXoJy\n" +
      "vBWRIL2gajImGU5fOoDQZJ3dGNgNh8anoe0/esMbdIZMFY6rAIWGiE+Y17+Or1UL\n" +
      "EBUen8o7Y+UCgYBWn17QeZWaF5wAj/cwC6Y0ubl73n3NzS4gpj8Spxuyy3hBFt3P\n" +
      "CUPaBa0Uef1U92JFgHs/s2Ajf95v7rOlOjB5gKGulDHk0gbAQU4BM8r2UHnrg/Yh\n" +
      "s6ed1fNp+bdCMnyQ+yH068G6F/BU7Qmcouuo0KtBoH7qdYa+MQj+5LLdkQKBgEdJ\n" +
      "56ZORLXOWexGWGqnqzfXUWSpVUqlTvkZPY0mYgJFhMj3PbDGGDIk2Kl3XaE/3LOU\n" +
      "a1IRNBIiAdutzyItKU5HqpglrJJcLOWQTqmvM/usaz+eHTBhOogmtZ+6C3/7Uw1t\n" +
      "rBghEDrdOvUYcaGxKdrc6Sen6dREMXvpueZdT+NJAoGAIsPaK0Rgu6Z540hiCF2M\n" +
      "0yYHriXljTAWtdm5FpCfoLwKox1OYLMQlFIXfN1qqmo6m13O+MW3IIU7X/aAk7T6\n" +
      "UW7GZybBe40J2AxVC48GX+jVk5iQjBzUtEf81jIZp61AD5KijNn33lHf653K09ch\n" +
      "uw+D9R3JrjzTHoyep6eif/s=\n" +
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
      "-----END CERTIFICATE-----"
    if (deviceId.isNullOrEmpty()) {
      deviceId = UUID.randomUUID().toString()
    }
    return LivenessRequest(
      duration = 600, privateKey = privateKey, appId = appId,
      deviceId = deviceId, clientTransactionId = clientTransactionId, secret = secret,
      baseURL = baseURL, publicKey = public_key
    )
  }
}
