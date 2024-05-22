package com.livenessrn.events

import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.WritableMap
import com.facebook.react.uimanager.events.Event
class RNLivenessEvent(
  surfaceId: Int,
  viewTag: Int,
  private val value: Int
) : Event<RNLivenessEvent>(surfaceId, viewTag)  {
  override fun getEventName(): String = "topChange"

  override fun getEventData(): WritableMap = Arguments.createMap().apply {
    putString("action", "onFrame")
    putInt("value", value)
  }
}

class RNLiveNessFinishEvent (
  surfaceId: Int,
  viewTag: Int,
  private val value: Any
) : Event<RNLiveNessFinishEvent>(surfaceId, viewTag) {

  override fun getEventName(): String = "topChange"
  override fun getEventData(): WritableMap = Arguments.createMap().apply {
    putString("action", "onFinished")
  }
}

