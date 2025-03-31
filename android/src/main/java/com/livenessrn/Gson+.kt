package com.livenessrn

import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.WritableMap
import com.nimbusds.jose.shaded.gson.Gson
import org.json.JSONObject

// Extension function để chuyển Gson model thành WritableMap
fun Any.toWritableMap(): WritableMap {
  val jsonString = Gson().toJson(this) // Convert object -> JSON String
  val jsonObject = JSONObject(jsonString) // Convert JSON String -> JSONObject
  return jsonObject.toWritableMap() // Chuyển JSONObject -> WritableMap
}

// Extension function để chuyển JSONObject thành WritableMap
fun JSONObject.toWritableMap(): WritableMap {
  val map = Arguments.createMap()
  keys().forEach { key ->
    when (val value = this[key]) {
      is Int -> map.putInt(key, value)
      is Double -> map.putDouble(key, value)
      is Boolean -> map.putBoolean(key, value)
      is String -> map.putString(key, value)
      is JSONObject -> map.putMap(key, value.toWritableMap()) // Đệ quy nếu là object
    }
  }
  return map
}
