//
//  LivenessView.swift
//  AppTest
//
//  Created by NamNg on 5/14/24.
//

import Foundation
import React
import UIKit
import LocalAuthentication
import FlashLiveness

@available(iOS 15.0, *)
class LivenessView: UIView, LivenessUtilityDetectorDelegate {
  var transactionId = ""
  var livenessDetector: LivenessUtilityDetector?
  var requestid = ""
  var appId = ""
  var baseUrl = ""
  var privateKey = ""
  var publicKey = ""
  var secret = "ABCDEFGHIJKLMNOP"
  var debugging = false
  var isFlashCamera = false
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
//            self.livenessDetector = LivenessUtilityDetector.createLivenessDetector(previewView: self,
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
          let colors: [UIColor] = [.red, .green, .blue]

          // Chọn một màu ngẫu nhiên
          if let randomColor: UIColor = colors.randomElement(){
              self.livenessDetector = LivenessUtil.createLivenessDetector(previewView: self, mode: .offline(filterColors: [randomColor]), delegate: self)
              try self.livenessDetector?.getVerificationRequiresAndStartSession(transactionId: self.transactionId)
          }
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
  
  @objc func setIsFlashCamera(_ val: Bool) {
    self.isFlashCamera = val as Bool
  }
  
  func liveness(liveness: LivenessUtilityDetector, didFail withError: LivenessError) {
    pushEvent(data: withError)
  }
  
    func liveness(_ liveness: LivenessUtilityDetector, didFinishWithResult result: LivenessResult) {
        let dataRes: [String: Any] = ["message": result.mess, "result": result, "code": result.code, "livenesScore": result.livenesScore, "status": result.status, "success": result.succes]
        pushEvent(data: dataRes)
    }
    
    func liveness(_ liveness: LivenessUtilityDetector, didFinishWithFaceImages images: LivenessFaceImages) {
        images.images?.forEach { image in
            let livenessImage = saveImageToFile(image: image) ?? ""
            let dataRes: [String: Any] = ["livenessImage": livenessImage]
              pushEvent(data: dataRes)
              livenessDetector?.stopLiveness()
        }
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
