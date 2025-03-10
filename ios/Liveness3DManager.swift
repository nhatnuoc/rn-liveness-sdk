//
//  Liveness3DManager.swift
//  QTSIdentityApp
//
//  Created by Nguyễn Thanh Bình on 21/12/24.
//

import QTSLiveness
//import FlashLiveness

@available(iOS 15.0, *)
class Liveness3DManager: LivenessManager {
  var success: ((_ response: [String: Any]) -> Void)?
    var failure: ((any Error) -> Void)?
    
    var livenessDetector: QTSLivenessDetector?
    var readIdCardRequestId: String?
//    var last_action: QTSLivenessAction?
//    var action_array: [QTSLivenessAction] = []
//    var player: AVAudioPlayer?
//    var maskView: LivenessMaskView?
    var livenessView: UIView
    
    init(previewView: UIView, readIdCardRequestId: String? = nil) {
//        let livenessView = UIView()
//        previewView.addSubview(livenessView)
//        self.livenessView = livenessView
      self.livenessView = previewView
        self.readIdCardRequestId = readIdCardRequestId
//        let maskView = LivenessMaskView(frame: previewView.bounds)
//        previewView.addSubview(maskView)
//        maskView.translatesAutoresizingMaskIntoConstraints = false
//        NSLayoutConstraint.activate([
//            maskView.topAnchor.constraint(equalTo: previewView.topAnchor),
//            maskView.bottomAnchor.constraint(equalTo: previewView.bottomAnchor),
//            maskView.leadingAnchor.constraint(equalTo: previewView.leadingAnchor),
//            maskView.trailingAnchor.constraint(equalTo: previewView.trailingAnchor),
//        ])
//        self.maskView = maskView
//        DispatchQueue.main.asyncAfter(deadline: .now() + 1, execute: {
//            livenessView.frame = maskView.previewFrame
//        })
    }
    
    func startLiveness() {
//        let additionHeader: [String: String] = [:]
//        self.livenessDetector = QTSLivenessDetector.createLivenessDetector(previewView: livenessView, threshold: .low, smallFaceThreshold: 0.25, debugging: false, delegate: self, livenessMode: .livenessAndVerifyFace, localLivenessThreshold: 0.98, calculationMode: .single, additionHeader: additionHeader)
        self.livenessDetector = QTSDepthLivenessDetector.init(
            previewView: self.livenessView,
            threshold: .low,
//            smallFaceThreshold: 0.25,
            debugging: false,
//            opacityOval: 0.0,
            livenessMode: .livenessAndVerifyFace,
//            localLivenessThreshold: 0.93,
            calculationMode: .combine
        )
        self.livenessDetector?.delegate = self
        Task {
            do {
                if let readCardRequestId = self.readIdCardRequestId {
                    let transaction = try await QTSLiveness.QTSNetworking.shared.initTransaction(additionParam: ["clientTransactionId":readCardRequestId])
                    try livenessDetector?.getVerificationRequiresAndStartSession(transactionId: transaction.data)
                } else {
                    let transaction = try await QTSLiveness.QTSNetworking.shared.initTransaction()
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
        QTSLiveness.QTSNetworking.shared.setup(appId: appId, logLevel: .debug, url: url, publicKey: publicKey, privateKey: privateKey)
        let resposne = QTSLiveness.QTSNetworking.shared.generateDeviceInfor()
        print(resposne)
    }
  
  static func registerFace(image: UIImage) async throws -> [String: Any] {
    let response = try await QTSNetworking.shared.registerFace(faceImage: image)
    return response.toJSON()
  }
}

@available(iOS 15.0, *)
extension Liveness3DManager: QTSLiveness.QTSLivenessUtilityDetectorDelegate {
    func liveness(liveness: QTSLivenessDetector, didFinish verificationImage: UIImage, livenesScore: Float, faceMatchingScore: Float, result: Bool, message: String, videoURL: URL?, response: QTSLivenessResult?) {
      var dic = response?.toJSON() ?? [:]
      dic["livenessImage"] = verificationImage.toBase64String(compressionQuality: 0.2)
      self.success?(dic)
    }
    
    func liveness(liveness: QTSLivenessDetector, didFail withError: QTSLivenessError) {
        self.failure?(withError)
    }
    
    func liveness(liveness: QTSLivenessDetector, startLivenessAction action: QTSLivenessAction) {
        
    }
}
