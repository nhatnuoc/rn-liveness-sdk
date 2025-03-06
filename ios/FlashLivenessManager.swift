//
//  FlashLivenessManager.swift
//  QTSIdentityApp
//
//  Created by Nguyễn Thanh Bình on 21/12/24.
//

import FlashLiveness
import UIKit

@available(iOS 13.0, *)
class FlashLivenessManager: LivenessManager {
  var success: ((_ response: [String: Any]) -> Void)?
    var failure: ((any Error) -> Void)?
    
    var livenessDetector: LivenessUtilityDetector?
    var readIdCardRequestId: String?
    
    init(previewView: UIView, readIdCardRequestId: String? = nil) {
        let additionHeader: [String: String] = [:]
      self.livenessDetector = LivenessUtil.createLivenessDetector(previewView: previewView, mode: .online, threshold: .low, delegate: self, brightnessEnable: false, additionHeader: additionHeader)
        self.readIdCardRequestId = readIdCardRequestId
    }
    
    func startLiveness() {
        Task {
            do {
                if let readCardRequestId = self.readIdCardRequestId {
                    let transaction = try await Networking.shared.initTransaction(additionParam: ["clientTransactionId":readCardRequestId])
                    try livenessDetector?.getVerificationRequiresAndStartSession(transactionId: transaction.data)
                } else {
                    let transaction = try await Networking.shared.initTransaction()
                    try livenessDetector?.getVerificationRequiresAndStartSession(transactionId: transaction.data)
                }
            } catch {
//                Constants.Alert.showErrorAlert(self, message: "Đã có lỗi xảy ra trong quá trình cài đặt cấu hình. Vui lòng thử lại.")
            }
        }
    }
    
    func stopLiveness() {
        self.livenessDetector?.stopLiveness()
    }
    
    static func setup(appId: String, url: String, publicKey: String, privateKey: String) {
        Networking.shared.setup(appId: appId, logLevel: .debug, url: url, publicKey: publicKey, privateKey: privateKey)
        let resposne = Networking.shared.generateDeviceInfor()
        print(resposne)
    }
  
  static func registerFace(image: UIImage) async throws -> [String: Any] {
    let response = try await Networking.shared.registerFace(faceImage: image)
    return response.toJSON()
  }
}

@available(iOS 13.0, *)
extension FlashLivenessManager: LivenessUtilityDetectorDelegate {
    func liveness(_ liveness: LivenessUtilityDetector, didFinishWithResult response: LivenessResult) {
      self.success?(response.toJSON())
    }
    
    func liveness(_ liveness: LivenessUtilityDetector, didFail withError: LivenessError) {
        self.failure?(withError)
    }
}
