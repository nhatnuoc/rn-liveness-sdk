///*
// * Copyright 2020 Google LLC. All rights reserved.
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *     http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//
//package com.livenessrnexample.util.mlkit;
//
//import android.content.Context;
//import android.content.SharedPreferences;
//import android.preference.PreferenceManager;
//
//import androidx.annotation.Nullable;
//import androidx.annotation.StringRes;
//
//import com.google.android.gms.common.images.Size;
//import com.google.common.base.Preconditions;
//
//import com.livenessrnexample.R;
//
///** Utility class to retrieve shared preferences. */
//public class PreferenceUtils {
//  @Nullable
//  public static CameraSource.SizePair getCameraPreviewSizePair(Context context, int cameraId) {
//    Preconditions.checkArgument(
//        cameraId == CameraSource.CAMERA_FACING_BACK
//            || cameraId == CameraSource.CAMERA_FACING_FRONT);
//    String previewSizePrefKey;
//    String pictureSizePrefKey;
//    if (cameraId == CameraSource.CAMERA_FACING_BACK) {
//      previewSizePrefKey = context.getString(R.string.pref_key_rear_camera_preview_size);
//      pictureSizePrefKey = context.getString(R.string.pref_key_rear_camera_picture_size);
//    } else {
//      previewSizePrefKey = context.getString(R.string.pref_key_front_camera_preview_size);
//      pictureSizePrefKey = context.getString(R.string.pref_key_front_camera_picture_size);
//    }
//
//    try {
//      SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
//      return new CameraSource.SizePair(
//          Size.parseSize(sharedPreferences.getString(previewSizePrefKey, null)),
//          Size.parseSize(sharedPreferences.getString(pictureSizePrefKey, null)));
//    } catch (Exception e) {
//      return null;
//    }
//  }
//
//  public static boolean shouldHideDetectionInfo(Context context) {
//    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
//    String prefKey = context.getString(R.string.pref_key_info_hide);
//    return sharedPreferences.getBoolean(prefKey, false);
//  }
//
//  /**
//   * Mode type preference is backed by {@link android.preference.ListPreference} which only support
//   * storing its entry value as string type, so we need to retrieve as string and then convert to
//   * integer.
//   */
//  private static int getModeTypePreferenceValue(
//      Context context, @StringRes int prefKeyResId, int defaultValue) {
//    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
//    String prefKey = context.getString(prefKeyResId);
//    return Integer.parseInt(sharedPreferences.getString(prefKey, String.valueOf(defaultValue)));
//  }
//
//  public static boolean isCameraLiveViewportEnabled(Context context) {
//    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
//    String prefKey = context.getString(R.string.pref_key_camera_live_viewport);
//    return sharedPreferences.getBoolean(prefKey, false);
//  }
//
//  private PreferenceUtils() {}
//}
