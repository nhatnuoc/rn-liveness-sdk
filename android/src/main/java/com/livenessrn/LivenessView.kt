package com.livenessrn

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.AttributeSet
import android.util.Base64
import android.util.Log
import android.widget.FrameLayout
import androidx.fragment.app.FragmentActivity
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.ReactContext
import com.facebook.react.bridge.WritableMap
import com.facebook.react.uimanager.ThemedReactContext
import com.facebook.react.uimanager.events.RCTEventEmitter
import com.liveness.sdk.corev4.LiveNessSDK
import com.liveness.sdk.corev4.model.LivenessModel
import com.liveness.sdk.corev4.utils.CallbackLivenessListener
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream


class LivenessView @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {
  private val callBack = object : CallbackLivenessListener {
    override fun onCallbackLiveness(data: LivenessModel?) {
      if (data?.status != null && data.status == 6666) {
        Log.d("CallBack back1", "CallBack back1")
        val activity = (context as ThemedReactContext).currentActivity as FragmentActivity
        activity.finish()
        return
      }

      if (data?.status != null && data.status == 200) {
        val map = Arguments.createMap()
        map.putInt("status", data.status ?: -1)
        map.putString("message", data.message ?: "")
        map.putString("request_id", data.requestId ?: "")
        map.putInt("code", 200)
        map.putBoolean("success", data.success ?: false)
        map.putString("pathVideo", data.pathVideo ?: "")
        map.putString("faceImage", data.faceImage ?: "")
        map.putString("livenessImage", data.livenessImage ?: "")
        map.putString("transactionID", data.transactionID ?: "")
        map.putString("livenesScore", "${data.data?.livenesScore}")
        map.putString("faceMatchingScore", "${data.data?.faceMatchingScore}")

        val mapData = Arguments.createMap()
        map.putString("faceMatchingScore", data.data?.faceMatchingScore ?: "")
        map.putString("livenessType", data.data?.livenessType ?: "")
        map.putDouble("livenesScore", (data.data?.livenesScore ?: 0).toDouble())
        map.putMap("data", mapData)
        callNativeEvent(map)
      } else {
        if (data?.imageResult.isNullOrEmpty() ) {
          val map = Arguments.createMap()
//        if (livenessModel?.action != null) {
//          map.putInt("action", livenessModel?.action ?: -1)
//          map.putString("message", livenessModel?.message ?: "")
//        } else {
//          map.putBoolean("status", false)
//          map.putString("message", livenessModel?.message ?: "")
//          map.putInt("code", 101)
//        }
          map.putBoolean("status", false)
          map.putString("message", data?.message ?: "")
          map.putInt("code", 101)
          callNativeEvent(map)
        }else {
          data?.imageResult?.apply {
            if(this.size>=2){
              // val originalImage = this[0].imagePath
              // val colorImage = this[1].imagePath
//               val originalImage = this[0].imagePath?.let { convertPathToBase64WithLimitKB(path = it) }
//               val colorImage = this[1].imagePath?.let { convertPathToBase64WithLimitKB(path = it) }
              val originalImage = this[0].image
              val colorImage = this[1].image
              val map = Arguments.createMap()
              map.putString("livenessImage", colorImage)
              map.putString("livenessOriginalImage", originalImage)
              map.putString("color", this[1].colorString)
              callNativeEvent(map)
            }else{
              val map = Arguments.createMap()
              map.putBoolean("status", false)
              map.putString("message", data?.message ?: "")
              map.putInt("code", 101)
              callNativeEvent(map)
            }
          }
        }

      }
    }
  }

  fun convertPathToBase64WithLimitKB(path: String, maxSizeInKB: Int = 400): String? {
    try {
      val file = File(path)
      if (!file.exists()) {
        throw IllegalArgumentException("File not found at path: $path")
      }

      // 1. Decode the image from the file path
      var bitmap = BitmapFactory.decodeFile(path)
        ?: throw IllegalArgumentException("Failed to decode image at path: $path")

      // 2. Compress and resize the image to reduce size
      var quality = 100 // Start with max quality
      var byteArray: ByteArray
      do {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream) // Compress as JPEG
        byteArray = outputStream.toByteArray()
        outputStream.close()

        // Reduce quality if size is still too large
        quality -= 5

        // Resize bitmap if needed
        if (byteArray.size > maxSizeInKB * 1024 && quality <= 5) {
          val newWidth = (bitmap.width * 0.9).toInt() // Reduce width by 10%
          val newHeight = (bitmap.height * 0.9).toInt() // Reduce height by 10%
          bitmap = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
          quality = 100 // Reset quality for resized image
        }
      } while (byteArray.size > maxSizeInKB * 1024 && quality > 0)

      // 3. Convert the byte array to Base64 string
      return Base64.encodeToString(byteArray, Base64.DEFAULT)

    } catch (e: Exception) {
      e.printStackTrace()
      return null // Return null if an error occurs
    }
  }

  fun base64ToPathFile(b64Data: String?, context: Context): String? {
    return try {
      val decodedBytes = Base64.decode(b64Data, Base64.DEFAULT)
      val file = File(context.cacheDir, "image_${System.currentTimeMillis()}.jpg")

      // Write the decoded bytes to the file
      FileOutputStream(file).use { outputStream ->
        outputStream.write(decodedBytes)
      }

      // Return the absolute path of the file
      file.absolutePath
    } catch (e: Exception) {
      e.printStackTrace()
      null
    }
  }

  fun base64ToBitmap(b64Data: String?): Bitmap? {
    return try {
      val decodedString = Base64.decode(b64Data, Base64.DEFAULT)
      val inputStream: InputStream = ByteArrayInputStream(decodedString)
      BitmapFactory.decodeStream(inputStream)
    } catch (e: Error) {
      e.printStackTrace()
      null
    }
  }

  init {
    LiveNessSDK.setCallbackListener(callBack)
  }

  fun callNativeEvent(map: WritableMap) {
    val reactContext = context as ReactContext
    val event = Arguments.createMap()
    event.putMap("data", map)
    reactContext.getJSModule(RCTEventEmitter::class.java).receiveEvent(
      id,
      "nativeClick",  //name has to be same as getExportedCustomDirectEventTypeConstants in MyCustomReactViewManager
      event
    )
  }
}
