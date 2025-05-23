package com.livenessrnexample

import android.app.Application
import com.facebook.react.PackageList
import com.facebook.react.ReactApplication
import com.facebook.react.ReactNativeHost
import com.facebook.react.ReactPackage
import com.facebook.react.defaults.DefaultNewArchitectureEntryPoint.load
import com.facebook.react.defaults.DefaultReactNativeHost
import com.facebook.soloader.SoLoader
import com.livenessrn.LivenessRnPackage
import com.livenessrnexample.native_module.CardReaderRnPackage


class MainApplication : Application(), ReactApplication {
  private val mReactNativeHost: ReactNativeHost = object : DefaultReactNativeHost(this) {
    override fun getUseDeveloperSupport(): Boolean {
      return BuildConfig.DEBUG
    }

    override fun getPackages(): List<ReactPackage> {
      return PackageList(this).packages.apply {
              // Packages that cannot be autolinked yet can be added manually here, for example:
//        add(LivenessRnPackage())
        add(CardReaderRnPackage())
      }
    }

    override fun getJSMainModuleName(): String {
      return "index"
    }

    override val isNewArchEnabled: Boolean
      protected get() = BuildConfig.IS_NEW_ARCHITECTURE_ENABLED
    override val isHermesEnabled: Boolean
      protected get() = BuildConfig.IS_HERMES_ENABLED
  }

  override fun getReactNativeHost(): ReactNativeHost {
    return mReactNativeHost
  }

  override fun onCreate() {
    super.onCreate()
    SoLoader.init(this,  /* native exopackage */false)
    if (BuildConfig.IS_NEW_ARCHITECTURE_ENABLED) {
      // If you opted-in for the New Architecture, we load the native entry point for this app.
      load()
    }
//    ReactNa.initializeFlipper(this, reactNativeHost.reactInstanceManager)
  }
}
