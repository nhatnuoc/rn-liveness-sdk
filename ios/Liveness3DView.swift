import Foundation
import React
import UIKit
import LocalAuthentication
@_implementationOnly import QTSLiveness

@available(iOS 15.0, *)
class Liveness3DView: UIView, LivenessUtilityDetectorDelegate {
  var transactionId = ""
  var livenessDetector: QTSLivenessDetector?
  var requestid = ""
  var appId = ""
  var baseUrl = ""
  var privateKey = ""
  var publicKey = ""
  var secret = "ABCDEFGHIJKLMNOP"
  var debugging = false
  var isDoneSmile = false
  
  override init(frame: CGRect) {
    super.init(frame: frame)
   setupView()
  }
 
  required init?(coder aDecoder: NSCoder) {
    super.init(coder: aDecoder)
   setupView()
  }
    
  private func setupConfig() {
    if !appId.isEmpty && !baseUrl.isEmpty && !publicKey.isEmpty && !privateKey.isEmpty && !requestid.isEmpty {
      Networking.shared.setup(appId: appId, logLevel: .debug, url: self.baseUrl, publicKey: self.publicKey, privateKey: self.privateKey)
      setupView()
    }
  }
 
  private func setupView() {
    // in here you can configure your view
    Task {
      do {
//        let response = try await Networking.shared.initTransaction(additionParam: ["clientTransactionId": self.requestid], clientTransactionId: self.requestid)
//        if response.status == 200 {
//          self.transactionId = response.data
//            self.livenessDetector = QTSLivenessDetector.createLivenessDetector(previewView: self,
//                                                                               threshold: .low,
//                                                                               smallFaceThreshold: 0.25,
//                                                                               debugging: true,
//                                                                               delegate: self,
//                                                                               livenessMode: .local,
//                                                                               additionHeader: ["header":"header"])
//          try self.livenessDetector?.getVerificationRequiresAndStartSession(transactionId: self.transactionId)
//        } else {
//          pushEvent(data: ["status" : response.status, "data": response.data, "signature": response.signature])
//        }
            self.livenessDetector = QTSLivenessDetector.createLivenessDetector(previewView: self,
                                                                               threshold: .low,
                                                                               smallFaceThreshold: 0.25,
                                                                               debugging: true,
                                                                               delegate: self,
                                                                               livenessMode: .local,
                                                                               additionHeader: ["header":"header"])
          try self.livenessDetector?.getVerificationRequiresAndStartSession(transactionId: self.transactionId)
      } catch {
        pushEvent(data: error)
      }
    }
  }
  
  private func pushEvent(data: Any) -> Void {
    if (self.onEvent != nil) {
      let event = ["data": data]
      self.onEvent!(event)
    }
  }
  
  @objc var onEvent: RCTBubblingEventBlock?
  
  @objc func setRequestid(_ val: NSString) {
    self.requestid = val as String
    self.setupConfig()
  }
    
  @objc func setAppId(_ val: NSString) {
    self.appId = val as String
    self.setupConfig()
  }
    
  @objc func setBaseUrl(_ val: NSString) {
    self.baseUrl = val as String
    self.setupConfig()
  }
    
  @objc func setPrivateKey(_ val: NSString) {
    self.privateKey = val as String
    self.setupConfig()
  }
    
  @objc func setPublicKey(_ val: NSString) {
    self.publicKey = val as String
    self.setupConfig()
  }
  
  @objc func setDebugging(_ val: Bool) {
    self.debugging = val as Bool
  }
  
  func liveness(liveness: QTSLivenessDetector, didFail withError: LivenessError) {
    pushEvent(data: withError)
  }
  
  func liveness(liveness: QTSLivenessDetector, didFinish verificationImage: UIImage, livenesScore: Float, faceMatchingScore: Float, result: Bool, message: String, videoURL: URL?, response: LivenessResult?) {
      let imageData = verificationImage.pngData()!
      let livenessImage = imageData.base64EncodedString(options: Data.Base64EncodingOptions.lineLength64Characters)
      let data = response?.data
      if(response?.status == 200) {
          let dataRes: [String: Any] = ["message": message, "livenessImage": livenessImage, "result": result, "code": 200, "livenesScore": livenesScore != 0 ? livenesScore : data!["livenesScore"] ?? 0, "request_id": response?.request_id ?? "", "status": response?.status ?? false, "success": response?.succes ?? false, "livenessType": data!["livenessType"] as? String ?? "", "faceMatchingScore": data!["faceMatchingScore"] as? String ?? "", "data": response?.data as Any]
        pushEvent(data: dataRes)
        livenessDetector?.stopLiveness()
      }  else {
          let dataRes: [String: Any] = ["message": message, "livenessImage": livenessImage, "result": result, "code": 101, "livenesScore": livenesScore, "status": response?.status ?? false, "success": response?.succes ?? false, "livenessType": data!["livenessType"] as? String ?? "", "faceMatchingScore": data!["faceMatchingScore"] as? String ?? "", "data": response?.data as Any]
          pushEvent(data: dataRes)
      }
//      Request id, message, status, success
  }

  func liveness(liveness: QTSLivenessDetector, startLivenessAction action: LivenessAction) {
    //  if action == .smile{
    //      isDoneSmile = false
    //    pushEvent(data: ["message": "check smile", "action": action.rawValue])
    //  } else if action == .fetchConfig{
    //      isDoneSmile = false
    //    pushEvent(data: ["message": "start check smile", "action": action.rawValue])
    //  } else if action == .detectingFace{
    //      isDoneSmile = false
    //    pushEvent(data: ["message": "detect face", "action": action.rawValue])
    //  } else if isDoneSmile == false{
    //      isDoneSmile = true
    //      pushEvent(data: ["message": "done smile", "action": action.rawValue])
    //  }
  }
    
    func liveness(liveness: QTSLivenessDetector, didFinishLocalLiveness score: Float, image: UIImage, videoURL: URL?) {
        let livenessImage = saveImageToFile(image: image) ?? ""
        let dataRes: [String: Any] = ["livenessImage": livenessImage,"livenesScore": score]
          pushEvent(data: dataRes)
          livenessDetector?.stopLiveness()
    }
    
    func saveImageToFile(image: UIImage) -> String? {
        // Chuyển đổi UIImage thành Data (PNG format)
        guard let imageData = image.pngData() else {
            print("Không thể chuyển đổi ảnh thành dữ liệu PNG")
            return nil
        }

        // Lấy đường dẫn thư mục tạm thời (temporary directory)
        let tempDirectory = FileManager.default.temporaryDirectory
        // Tạo tên file với phần mở rộng .png
        let fileName = "face_authentications" + ".png"
        // Tạo đường dẫn đầy đủ cho file
        let fileURL = tempDirectory.appendingPathComponent(fileName)
        
        do {
            // Lưu dữ liệu PNG vào file
            try imageData.write(to: fileURL)
            print("Đã lưu file tại: \(fileURL.path)")
            // Trả về đường dẫn của file
            return fileURL.path
        } catch {
            print("Không thể lưu ảnh: \(error.localizedDescription)")
            return nil
        }
    }

    
    
  func stopLiveness() {
    livenessDetector?.stopLiveness()
  }
    
  var faceIDAvailable: Bool {
    if #available(iOS 11.0, *) {
      let context = LAContext()
      return (context.canEvaluatePolicy(LAPolicy.deviceOwnerAuthentication, error: nil) && context.biometryType == .faceID)
    }
    return false
  }
}
