import UIKit
import Foundation
import React
import AVFoundation

@available(iOS 13.0, *)
@objc(RCTLivenessViewManager)
class RCTLivenessViewManager: RCTViewManager {

    override static func requiresMainQueueSetup() -> Bool {
        return true
    }

    override func view() -> UIView! {
        return LivenessView()
    }
  
  @objc func startLiveness(_ node: NSNumber) {
      DispatchQueue.main.async {
        if let view = self.bridge.uiManager.view(forReactTag: node) as? LivenessView {
          view.startLiveness()
        }
      }
    }
}
