import Foundation
import React
import UIKit

@available(iOS 13.0, *)
class LivenessView: UIView {
  var debugging = false
  private var originalBrightness: CGFloat?
  var livenessManager: LivenessManager?
  
  override init(frame: CGRect) {
    super.init(frame: frame)
    //   setupView()
    configution()
    registerForNotifications()
  }
  
  required init?(coder aDecoder: NSCoder) {
    super.init(coder: aDecoder)
    //   setupView()
    configution()
    registerForNotifications()
  }
  
  func configution() {
    self.backgroundColor = .clear
    if UIScreen.main.brightness == 1 {
      self.originalBrightness = 0.35
    } else {
      self.originalBrightness = UIScreen.main.brightness
    }
  }
  
  deinit {
    revertLightScreen()
    print("Dispose Liveness")
    resetLivenessDetector()
    unregisterFromNotifications()
  }
  
  override func didMoveToSuperview() {
    super.didMoveToSuperview()
    if superview != nil {
      //            upLightScreen()
      print("LivenessView đã được thêm vào màn hình.")
      // Thực hiện các tác vụ cần thiết
    } else {
      revertLightScreen()
      print("LivenessView đã bị xoá khỏi màn hình.")
      resetLivenessDetector()
    }
  }
  
  private func registerForNotifications() {
    NotificationCenter.default.addObserver(self, selector: #selector(onEnterBackground), name: UIApplication.didEnterBackgroundNotification, object: nil)
    NotificationCenter.default.addObserver(self, selector: #selector(onEnterForeground), name: UIApplication.willEnterForegroundNotification, object: nil)
  }
  
  private func unregisterFromNotifications() {
    NotificationCenter.default.removeObserver(self, name: UIApplication.didEnterBackgroundNotification, object: nil)
    NotificationCenter.default.removeObserver(self, name: UIApplication.willEnterForegroundNotification, object: nil)
  }
  
  @objc private func onEnterBackground() {
    // Handle entering background
    print("App entered background")
    revertLightScreen()
  }
  
  @objc private func onEnterForeground() {
    // Handle entering foreground
    print("App entered foreground")
    upLightScreen()
  }
  
  func upLightScreen() {
    revertLightScreen()
    
    // Lưu độ sáng ban đầu nếu chưa lưu
    //        if self.originalBrightness == nil {
    //            self.originalBrightness = UIScreen.main.brightness
    //        }
    
    // Tăng độ sáng lên mức tối đa
    DispatchQueue.main.asyncAfter(deadline: .now() + 0.2) {
      UIScreen.main.brightness = 1.0
    }
  }
  
  func revertLightScreen() {
    // Khôi phục độ sáng ban đầu nếu đã được lưu
    if let brightness = self.originalBrightness {
      UIScreen.main.brightness = brightness
      //            self.originalBrightness = nil
    }
    
    //        UIScreen.main.brightness = 0.35
    print("Brightness revert to")
  }
  
  private func setupConfig() {
    upLightScreen()
    resetLivenessDetector()
    setupLiveness()
  }
  
  private func resetLivenessDetector() {
    self.livenessManager?.stopLiveness()
    self.livenessManager = nil
    removeFromSuperview()
  }
  
  @objc private func setupLiveness() {
    self.livenessManager = LivenessManagerFactory.instantiate(previewView: self, appId: self.appId, baseUrl: self.baseURL, publicKey: self.publicKey, privateKey: self.privateKey, readIdCardRequestId: self.idCardRequestId, useFlash: self.useFlash)
    self.livenessManager?.success = { [weak self] response in
      self?.onSuccess?(["response": response])
    }
    self.livenessManager?.failure = { [weak self] error in
      self?.onFailure?(["error": error])
    }
    self.startLiveness()
  }
  
  @objc
  func startLiveness() {
    self.livenessManager?.startLiveness()
  }
  
  @objc override func responds(to aSelector: Selector!) -> Bool {
    if aSelector == #selector(startLiveness) {
      return true
    }
    return super.responds(to: aSelector)
  }
  
  private func propertyChanged() {
    NSObject.cancelPreviousPerformRequests(withTarget: self, selector: #selector(setupLiveness), object: nil)
    self.perform(#selector(setupLiveness), with: nil, afterDelay: 0.1)
  }
  
  @objc var onSuccess: RCTBubblingEventBlock?
  @objc var onFailure: RCTBubblingEventBlock?
  @objc var idCardRequestId: String? {
    didSet {
      self.propertyChanged()
    }
  }
  @objc var useFlash: Bool = false {
    didSet {
      self.propertyChanged()
    }
  }
  @objc var appId: String? {
    didSet {
      self.propertyChanged()
    }
  }
  @objc var baseURL: String? {
    didSet {
      self.propertyChanged()
    }
  }
  @objc var publicKey: String? {
    didSet {
      self.propertyChanged()
    }
  }
  @objc var privateKey: String? {
    didSet {
      self.propertyChanged()
    }
  }
  
  @objc func setDebugging(_ val: Bool) {
    print("9999")
    self.debugging = val as Bool
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
    print("convert done")
    return base64String
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

