# rn-liveness-sdk

LivenessSDK - React Native

## Request
  * Minimum iOS Deployment Target: 13.0
  * Xcode 14 or newer
  * Swift 5
  * Android minSdkVersion: 24
  * Android compileSdkVersion: 36
  * Android targetSdkVersion: 36
  * React Native version >= 0.73

## Installation

package.json

```json
"dependencies": {
    "liveness-rn": "https://github.com/nhatnuoc/rn-liveness-sdk.git#{tag_version}",
}
```

## Manually Installation

#### Android

1. In root android's project -> add to `build.gradle`

```java
repositories {
    google()
    mavenCentral()
    maven { url 'https://jitpack.io' }

}
```

add to dependencies

```dependencies
    implementation("com.facebook.react:react-android")
    implementation('androidx.appcompat:appcompat:1.4.1')
    implementation 'androidx.constraintlayout:constraintlayout:2.1.3'

    implementation('com.google.mlkit:face-detection:16.1.5')
    implementation('com.otaliastudios:cameraview:2.7.2')
    implementation('org.bouncycastle:bcpkix-jdk18on:1.73')
    implementation('com.nimbusds:nimbus-jose-jwt:9.31')
    implementation('commons-codec:commons-codec:1.16.0')
```

#### IOS
```
After pod installation is complete
```

Add permissions in Info.plist

``` Add permissions in Info.plist
<key>LSRequiresIPhoneOS</key>
	<true/>
	<key>NSAppTransportSecurity</key>
	<dict>
		<key>NSAllowsArbitraryLoads</key>
		<false/>
		<key>NSAllowsLocalNetworking</key>
		<true/>
	</dict>
	<key>NSCameraUsageDescription</key>
	<string>Use to scan QR code and take photos of user&apos;s certificate and uses the camera to read passports</string>
	<key>NSLocationWhenInUseUsageDescription</key>
	<string></string>
	<key>NSMicrophoneUsageDescription</key>
	<string>The application does not use this feature</string>
	<key>NSPhotoLibraryAddUsageDescription</key>
	<string>The application does not use this feature</string>
	<key>NSPhotoLibraryUsageDescription</key>
	<string>The application does not use this feature</string>
	<key>NSDocumentsDirectory</key>
	<string>The application does not use this feature</string>
	<key>UILaunchStoryboardName</key>
	<string>LaunchScreen</string>
	<key>UIRequiredDeviceCapabilities</key>
	<array>
		<string>arm64</string>
	</array>
```

### add Podfile

add library
```
  <!-- pod 'QTSCardReader' ,:git => 'https://github.com/trungnguyen1791/QTSCardReader.git'
  pod 'SVProgressHUD'
  pod 'Alamofire', '5.8.1'
  pod 'QTSLiveness'
  pod 'FlashLiveness', :git => 'https://github.com/stevienguyen1988/FlashLivenessPod.git'
```

```
    use_frameworks!
    ***
    installer.pods_project.targets.each do |target|
      target.build_configurations.each do |config|
        config.build_settings['BUILD_LIBRARY_FOR_DISTRIBUTION'] = 'YES'
      end
    end
```
```
  installer.pods_project.targets.each do |target|
      target.build_configurations.each do |config|
         if config.build_settings['IPHONEOS_DEPLOYMENT_TARGET'].to_f < 12.0
           config.build_settings['IPHONEOS_DEPLOYMENT_TARGET'] = '11.0'
         end
      end
  end
```

#### React native
#####  All ways to use the sdk have been detailed as examples in the sample folder
``` File example
path example/src/App.js
```

# Implement
Nhúng component LivenessView để hiển thị giao diện Liveness
```markdown
import {
    LivenessView
} from 'liveness-rn';

<LivenessView
            style={
              Platform.OS === 'ios' ? styles.view_liveness :
                {
                  height: PixelRatio.getPixelSizeForLayoutSize(layout.height),
                  width: PixelRatio.getPixelSizeForLayoutSize(layout.width),
                }
            }
            onSuccess={(event) => {
              let response = event.nativeEvent.response
              Alert.alert("Thông báo", JSON.stringify(response))
              console.log(JSON.stringify(response))
            }}
            idCardRequestId={text}
            debugging={false}
            // useFlash={true}
            appId={'com.pvcb'}
            baseURL={'https://ekyc-sandbox.eidas.vn/face-matching'}
            publicKey={publicKey}
            privateKey={privateKey}
          />
```

# Callback when liveness success
```js
onSuccess={(event) => {
              let response = event.nativeEvent.response
              Alert.alert("Thông báo", JSON.stringify(response))
              console.log(JSON.stringify(response))
            }}
```

=> + Lưu ý màn livenesss style nên để widht: 100%, height: Platform.OS == 'android' ? windowWidth * 1.7 : '100%'
  + Để khung hình camera vào giữa màn hình

## Contributing

See the [contributing guide](CONTRIBUTING.md) to learn how to contribute to the repository and the development workflow.

## License

MIT

---

## Author
Nguyễn Thanh Bình
