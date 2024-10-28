# liveness-rn

Liveness - React Native

## Request
  * Minimum iOS Deployment Target: 13.0
  * Xcode 14 or newer
  * Swift 5
  * Android minSdkVersion: 24
  * Android compileSdkVersion: 33
  * Android targetSdkVersion: 33
  * React Native version < 0.73

## Installation

package.json

```json
"dependencies": {
    "liveness-rn": "https://github.com/Techainer/rn-sdk.git#{tag_version}",
}
```
or
```json
"dependencies": {
    "liveness-rn": "file://{path_to_folder_clone}/rn-sdk",
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

#### IOS
```
After pod installation is complete, copy the file "LivenessUtility.xcframework" to /Pods/LivenessCloud/LivenessUtility.

The file is placed in the ios_fameworks path.

```
```
add Podfile:
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

import PVcomBankPayment from 'liveness-rn';

```
getDeviceId:
  getDeviceId((data) => {
    // todo deviceId
  });

registerFace:
  registerFace(imageBase64, (data) => {
   // todo data registerFace
  });
```

## Contributing

See the [contributing guide](CONTRIBUTING.md) to learn how to contribute to the repository and the development workflow.

## License

MIT

---

Made with [create-react-native-library](https://github.com/callstack/react-native-builder-bob)
