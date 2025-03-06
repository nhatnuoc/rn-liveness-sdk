//
//  LivenessManager.swift
//  QTSIdentityApp
//
//  Created by Nguyễn Thanh Bình on 21/12/24.
//

import UIKit
import AVFoundation

protocol LivenessManager {
  var success: ((_ response: [String: Any]) -> Void)? { get set }
  var failure: ((Error) -> Void)? { get set }
  func startLiveness()
  func stopLiveness()
  static func setup(appId: String, url: String, publicKey: String, privateKey: String)
  @available(iOS 13.0.0, *)
  static func registerFace(image: UIImage) async throws -> [String: Any]
}

class LivenessManagerFactory {
  static func isTrueDepthCameraSupported() -> Bool {
    let discoverySession = AVCaptureDevice.DiscoverySession(
      deviceTypes: [.builtInTrueDepthCamera],
      mediaType: .video,
      position: .front
    )
    guard !discoverySession.devices.isEmpty else { return false }
    guard let device = AVCaptureDevice.default(.builtInTrueDepthCamera, for: .video, position: .front) else {
      print("TrueDepth Camera không có sẵn trên thiết bị.")
      return false
    }
    
    do {
      let input = try AVCaptureDeviceInput(device: device)
      let session = AVCaptureSession()
      session.addInput(input)
      return true
    } catch {
      print("Không thể khởi tạo phiên chụp với TrueDepth Camera: \(error.localizedDescription)")
      return false
    }
    //        return true
  }
  
  static func instantiate(previewView: UIView, appId: String?, baseUrl: String?, publicKey: String?, privateKey: String?, readIdCardRequestId: String? = nil, useFlash: Bool = false) -> LivenessManager? {
    guard let appId = appId, let baseUrl = baseUrl, let publicKey = publicKey, let privateKey = privateKey else {
      return nil
    }
    self.setup(appId: appId, url: baseUrl, publicKey: publicKey, privateKey: privateKey)
    if self.isTrueDepthCameraSupported(), #available(iOS 15.0, *), !useFlash {
      return Liveness3DManager(previewView: previewView, readIdCardRequestId: readIdCardRequestId)
    }
    if #available(iOS 13.0, *) {
      return FlashLivenessManager(previewView: previewView, readIdCardRequestId: readIdCardRequestId)
    }
    return nil
  }
  
  static func setup(appId: String, url: String, publicKey: String, privateKey: String) {
    if self.isTrueDepthCameraSupported(), #available(iOS 15.0, *) {
      Liveness3DManager.setup(appId: appId, url: url, publicKey: publicKey, privateKey: privateKey)
    }
    if #available(iOS 13.0, *) {
      FlashLivenessManager.setup(appId: appId, url: url, publicKey: publicKey, privateKey: privateKey)
    }
  }
  
  @available(iOS 13.0.0, *)
  static func registerFace(image: UIImage, useFlash: Bool = false) async throws -> [String: Any] {
//    if self.isTrueDepthCameraSupported(), #available(iOS 15.0, *), !useFlash {
//      return try await Liveness3DManager.registerFace(image: image)
//    }
//    return try await FlashLivenessManager.registerFace(image: image)
    let flash = try await FlashLivenessManager.registerFace(image: image)
    if #available(iOS 15.0, *), !useFlash {
      return try await Liveness3DManager.registerFace(image: image)
    }
    return flash
  }
}
