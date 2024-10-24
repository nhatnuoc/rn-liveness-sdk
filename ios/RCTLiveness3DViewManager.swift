import UIKit
import Foundation
import React
import AVFoundation

@available(iOS 15.0, *)
@objc (RCTLiveness3DViewManager)
class RCTLiveness3DViewManager: RCTViewManager {
    override static func requiresMainQueueSetup() -> Bool {
        return true
    }

    override func view() -> UIView! {
        return Liveness3DView()
    }
}
