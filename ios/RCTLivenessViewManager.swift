import UIKit
import Foundation
import React
import AVFoundation

@available(iOS 15.0, *)
@objc (RCTLivenessViewManager)
class RCTLivenessViewManager: RCTViewManager {
    
    var isFlashCamera: Bool = false
    
    @objc func setIsFlashCamera(_ isFlashCamera: Bool) {
        self.isFlashCamera = isFlashCamera
    }

    override static func requiresMainQueueSetup() -> Bool {
        return true
    }

    override func view() -> UIView! {
        if isFlashCamera {
            return LivenessView()
        }

        return Liveness3DView()
    }
}
