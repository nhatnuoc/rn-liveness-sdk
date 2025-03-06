import React
import UIKit

@available(iOS 13.0, *)
@objc(LivenessRn)
class LivenessRn: NSObject {
  @objc(setupLiveness:baseURL:publicKey:privateKey:)
  func setupLiveness(appId: String, baseURL: String, publicKey: String, privateKey: String) {
    LivenessManagerFactory.setup(appId: appId, url: baseURL, publicKey: publicKey, privateKey: privateKey)
  }
  
  @objc(registerFace:resolve:reject:)
  func registerFace(imageUri: String, resolve: @escaping RCTPromiseResolveBlock, reject: @escaping RCTPromiseRejectBlock) -> Void {
    do {
      guard let imageUri = URL(string: imageUri) else {
        reject("FACE_IMAGE_URI_ERROR", "face image uri error", NSError(domain: "FACE_IMAGE_URI_ERROR", code: 0, userInfo: [NSLocalizedDescriptionKey: "face image uri error"]))
        return
      }
      let dataAvatar = try Data(contentsOf: imageUri, options: .alwaysMapped)
      guard let uiimage = UIImage(data: dataAvatar) else {
        reject("FACE_IMAGE_DATA_ERROR", "face image data error", NSError(domain: "FACE_IMAGE_DATA_ERROR", code: 0, userInfo: [NSLocalizedDescriptionKey: "face image data error"]))
        return
      }
      Task {
        let response = try await LivenessManagerFactory.registerFace(image: uiimage)
        resolve(response)
      }
    } catch {
      reject("REGISTER_FACE_ERROR", "register face error", error)
    }
  }
}
