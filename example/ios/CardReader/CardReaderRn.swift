//
//  CardReaderRn.swift
//  LivenessRnExample
//
//  Created by Nguyễn Thanh Bình on 7/5/25.
//

import UIKit
import IDCardReader
import React

@objc(CardReaderRn)
class CardReaderRn: NSObject {
  @objc(readCard:baseURL:publicKey:privateKey:resolve:reject:)
  func readCard(appId: String, baseURL: String, publicKey: String, privateKey: String, resolve: @escaping RCTPromiseResolveBlock, reject: @escaping RCTPromiseRejectBlock) {
    IDCardReaderManager.shared.setup(serverUrl: baseURL, appId: appId, publicKey: publicKey, privateKey: privateKey)
    IDCardReaderManager.shared.setLocalizeTexts(reading: "Reading")
    IDCardReaderManager.shared.setLocalizeTexts(requestPresentCard: "Vui lòng đưa CCCD vào",
                                                authenticating: "Đang xác thực",
                                                reading: "Đang đọc",
                                                errorReading: "CCCD không hợp lệ. Vui lòng kiểm tra lại.",
                                                successReading: "Đọc thẻ thành công",
                                                retry: "Đang thử lại")
    DispatchQueue.main.async(execute: {
      if let tessdataPath = Bundle(for: QKMRZScannerView.self).path(forResource: "tessdata", ofType: nil) {
              let trainedDataPath = (tessdataPath as NSString).appendingPathComponent("ocrb.traineddata")
              
              let fileExists = FileManager.default.fileExists(atPath: trainedDataPath)
              print("✅ TESSDATA_PATH: \(tessdataPath)")
              print("🔍 Checking file: \(trainedDataPath)")
              print(fileExists ? "✅ File exists." : "❌ File does NOT exist.")
          } else {
              print("❌ Could not find tessdata folder in bundle.")
          }
      let vc = ShowRegistrationScanMRZViewController.storyboardViewController
      vc.resolve = resolve
      vc.reject = reject
      let nav = UINavigationController(rootViewController: vc)
      UIApplication.shared.keyWindow?.rootViewController?.present(nav, animated: true)
    })
  }
}
