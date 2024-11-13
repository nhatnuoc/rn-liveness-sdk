import UIKit
import Foundation
import React
import AVFoundation

@available(iOS 13.0, *)
@objc (RCTLivenessViewManager)
class RCTLivenessViewManager: RCTViewManager {

    override static func requiresMainQueueSetup() -> Bool {
        return true
    }

    override func view() -> UIView! {
        return LivenessView()
    }
}
