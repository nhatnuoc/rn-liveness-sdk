import Foundation
import React
import UIKit
import LocalAuthentication
import FlashLiveness
@_implementationOnly import QTSLiveness

@available(iOS 13.0, *)
class LivenessView: UIView, QTSLiveness.LivenessUtilityDetectorDelegate, FlashLiveness.LivenessUtilityDetectorDelegate {
  var transactionId = ""
  var livenessDetector: Any?
  private var viewMask: LivenessMaskView!
  var requestid = ""
  var appId = ""
  var baseUrl = ""
  var privateKey = ""
  var publicKey = ""
  var secret = "ABCDEFGHIJKLMNOP"
  var debugging = false
  var isFlashCamera = false
  var isDoneSmile = false
    
    override func layoutSubviews() {
        super.layoutSubviews()
        if let viewMask = viewMask {
            viewMask.frame = self.bounds // Ensure viewMask covers the entire view
        } // Ensure viewMask covers the entire view
    }
  
  override init(frame: CGRect) {
    super.init(frame: frame)
//   setupView()
  }
 
  required init?(coder aDecoder: NSCoder) {
    super.init(coder: aDecoder)
//   setupView()
  }
    
  private func setupConfig() {
      resetLivenessDetector()
      setupView()
  }
    private func resetLivenessDetector() {
        removeFromSuperview()
        if let detector = livenessDetector as? FlashLiveness.LivenessUtilityDetector {
            // Reset specific configurations or data for FlashLiveness if needed
            detector.stopLiveness() // Stop the session for FlashLiveness
            print("FlashLiveness detector stopped and reset.")
        } else if let detector = livenessDetector as? QTSLiveness.QTSLivenessDetector {
            // Reset specific configurations or data for QTSLiveness if needed
            detector.stopLiveness() // Stop the session for QTSLiveness
            print("QTSLiveness detector stopped and reset.")
        }
        
        livenessDetector = nil // Set to nil to allow reinitialization
    }

 
  private func setupView() {
      do {
                  if isFlashCamera {
                      let colors: [UIColor] = [.red, .green, .blue]
                      if let randomColor = colors.randomElement() {
                          // FlashLiveness setup
                          self.livenessDetector = FlashLiveness.LivenessUtil.createLivenessDetector(
                              previewView: self,
                              mode: .offline(filterColors: [randomColor]),
                              debugging: debugging,
                              delegate: self
                          )
                      }
                  } else {
                      // QTSLiveness setup
                      self.livenessDetector = QTSLiveness.QTSLivenessDetector.createLivenessDetector(
                          previewView: self,
                          threshold: .low,
                          smallFaceThreshold: 0.25,
                          debugging: debugging,
                          delegate: self,
                          livenessMode: .local,
                          calculationMode: .multiple,
                          additionHeader: ["header": "header"]
                      )
                      viewMask = LivenessMaskView(frame: bounds)
                      viewMask.backgroundColor = UIColor.clear
                      viewMask.layer.zPosition = 1 // Bring viewMask to the top layer
                      addSubview(viewMask)
                  }
                  
                  // Starting the session only if livenessDetector was successfully created
                  try startSession()
                  
              } catch {
                  pushEvent(data: ["error": error.localizedDescription])
              }
  }
    
    private func startSession() throws {
            guard let detector = livenessDetector else {
                throw NSError(domain: "LivenessError", code: 0, userInfo: [NSLocalizedDescriptionKey: "Liveness Detector could not be initialized"])
            }

            if isFlashCamera, let flashDetector = detector as? FlashLiveness.LivenessUtilityDetector {
                try flashDetector.getVerificationRequiresAndStartSession(transactionId: self.transactionId)
            } else if let qtDetector = detector as? QTSLiveness.QTSLivenessDetector {
                try qtDetector.getVerificationRequiresAndStartSession(transactionId: self.transactionId)
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
      print("9999")
    self.requestid = val as String
//    self.setupConfig()
  }
    
  @objc func setAppId(_ val: NSString) {
      print("9999")
    self.appId = val as String
//    self.setupConfig()
  }
    
  @objc func setBaseUrl(_ val: NSString) {
      print("9999")
    self.baseUrl = val as String
//    self.setupConfig()
  }
    
  @objc func setPrivateKey(_ val: NSString) {
      print("9999")
    self.privateKey = val as String
//    self.setupConfig()
  }
    
  @objc func setPublicKey(_ val: NSString) {
      print("9999")
    self.publicKey = val as String
//    self.setupConfig()
  }
  
  @objc func setDebugging(_ val: Bool) {
      print("9999")
    self.debugging = val as Bool
  }
    
  @objc func setIsFlashCamera(_ val: Bool) {
        print("9999")
        self.isFlashCamera = val as Bool
        self.setupConfig()
  }
  
    private func liveness(liveness: FlashLiveness.LivenessUtilityDetector, didFail withError: FlashLiveness.LivenessError) {
    pushEvent(data: withError)
  }
  
    func liveness(_ liveness: FlashLiveness.LivenessUtilityDetector, didFinishWithResult result:  FlashLiveness.LivenessResult) {
        let dataRes: [String: Any] = ["message": result.mess, "result": result, "code": result.code, "livenesScore": result.livenesScore, "status": result.status, "success": result.succes]
        pushEvent(data: dataRes)
    }
    
    func liveness(_ liveness: FlashLiveness.LivenessUtilityDetector, didFinishWithFaceImages images: FlashLiveness.LivenessFaceImages) {
        let livenessImage = images.images.first.imagePath
        let livenessOriginalImage = images.originalImage.imagePath
        let dataRes: [String: Any] = [
            "livenessImage": livenessImage,
            "livenessOriginalImage": livenessOriginalImage,
            "color": images.images.first.hexColorString,
        ]
          pushEvent(data: dataRes)
        (livenessDetector as! LivenessUtilityDetector).stopLiveness()
    }
    
    
    func liveness(liveness: QTSLiveness.QTSLivenessDetector, didFail withError: QTSLiveness.LivenessError) {
      pushEvent(data: withError)
    }
    
//    func liveness(liveness: QTSLivenessDetector, didFinish verificationImage: UIImage, livenesScore: Float, faceMatchingScore: Float, result: Bool, message: String, videoURL: URL?, response: LivenessResult?) {
//        let imageData = verificationImage.pngData()!
//        let livenessImage = imageData.base64EncodedString(options: Data.Base64EncodingOptions.lineLength64Characters)
//        let data = response?.data
//        if(response?.status == 200) {
//            let dataRes: [String: Any] = ["message": message, "livenessImage": livenessImage, "result": result, "code": 200, "livenesScore": livenesScore != 0 ? livenesScore : data!["livenesScore"] ?? 0, "request_id": response?.request_id ?? "", "status": response?.status ?? false, "success": response?.succes ?? false, "livenessType": data!["livenessType"] as? String ?? "", "faceMatchingScore": data!["faceMatchingScore"] as? String ?? "", "data": response?.data as Any]
//          pushEvent(data: dataRes)
//          livenessDetector?.stopLiveness()
//        }  else {
//            let dataRes: [String: Any] = ["message": message, "livenessImage": livenessImage, "result": result, "code": 101, "livenesScore": livenesScore, "status": response?.status ?? false, "success": response?.succes ?? false, "livenessType": data!["livenessType"] as? String ?? "", "faceMatchingScore": data!["faceMatchingScore"] as? String ?? "", "data": response?.data as Any]
//            pushEvent(data: dataRes)
//        }
//  //      Request id, message, status, success
//    }
      
    func liveness(liveness: QTSLiveness.QTSLivenessDetector, didFinishLocalLiveness score: Float, maxtrix:[Float], image: UIImage, videoURL: URL?) {
          let livenessImage = saveImageToFile(image: image, isOriginal: false) ?? ""
        let dataRes: [String: Any] = [
            "livenessOriginalImage": livenessImage,
            "vector": maxtrix,
        ]
            pushEvent(data: dataRes)
        (livenessDetector as! QTSLivenessDetector).stopLiveness()
      }
    
    func saveImageToFile(image: UIImage, isOriginal: Bool) -> String? {
        // Chuyển đổi UIImage thành Data (PNG format)
        guard let imageData = image.pngData() else {
            print("Không thể chuyển đổi ảnh thành dữ liệu PNG")
            return nil
        }

        // Lấy đường dẫn thư mục tạm thời (temporary directory)
        let tempDirectory = FileManager.default.temporaryDirectory
        // Tạo tên file với phần mở rộng .png
        let fileName = "face_authentications\(isOriginal ? "_original" : "")" + ".png"
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

    func convertImageToBase64(_ image: UIImage) -> String? {
        // Convert UIImage to Data in JPEG format with optional compression quality
        guard let imageData = image.jpegData(compressionQuality: 1.0) else { return nil }
        
        // Convert the Data to a Base64 encoded string
        let base64String = imageData.base64EncodedString(options: .lineLength64Characters)
        return base64String
    }


  func stopLiveness() {
      (livenessDetector as AnyObject).stopLiveness()
  }
    
  var faceIDAvailable: Bool {
    if #available(iOS 11.0, *) {
      let context = LAContext()
      return (context.canEvaluatePolicy(LAPolicy.deviceOwnerAuthentication, error: nil) && context.biometryType == .faceID)
    }
    return false
  }
}

extension UIColor {
    func toHexDouble(includeAlpha: Bool = false) -> Double {
        var red: CGFloat = 0
        var green: CGFloat = 0
        var blue: CGFloat = 0
        var alpha: CGFloat = 0

        self.getRed(&red, green: &green, blue: &blue, alpha: &alpha)

        let rgb: Int
        if includeAlpha {
            rgb = (Int(red * 255) << 24) | (Int(green * 255) << 16) | (Int(blue * 255) << 8) | Int(alpha * 255)
        } else {
            rgb = (Int(red * 255) << 16) | (Int(green * 255) << 8) | Int(blue * 255)
        }

        return Double(rgb)
    }
}

