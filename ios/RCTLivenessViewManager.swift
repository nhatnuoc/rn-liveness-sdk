import UIKit
import Foundation
import React
import AVFoundation

@available(iOS 15.0, *)
@objc (RCTLivenessViewManager)
class RCTLivenessViewManager: RCTViewManager {

    override static func requiresMainQueueSetup() -> Bool {
        return true
    }

    override func view() -> UIView! {
       if is3DCameraSupported() {
           if self.is3DCameraError() {
               return LivenessView()
           }

           return Liveness3DView()
       } else {
           // Return LivenessView for older devices
           return LivenessView()
       }
    }

    // Check if the device supports 3D camera (e.g., iPhone X and later)
    private func is3DCameraSupported() -> Bool {
        let device = UIDevice.current
        let modelName = device.modelName
        print("--------\(modelName)")

        let supportedModels = [
            // iPhone X series
            "iPhone X", "iPhone XS", "iPhone XS Max", "iPhone XR",
            
            // iPhone 11 series
            "iPhone 11", "iPhone 11 Pro", "iPhone 11 Pro Max",
            
            // iPhone 12 series
            "iPhone 12", "iPhone 12 mini", "iPhone 12 Pro", "iPhone 12 Pro Max",
            
            // iPhone 13 series
            "iPhone 13", "iPhone 13 mini", "iPhone 13 Pro", "iPhone 13 Pro Max",
            
            // iPhone 14 series
            "iPhone 14", "iPhone 14 Plus", "iPhone 14 Pro", "iPhone 14 Pro Max",
            
            // iPhone 15 series (giả định tên mã)
            "iPhone 15", "iPhone 15 Plus", "iPhone 15 Pro", "iPhone 15 Pro Max",
            
            // iPhone 16 series (giả định tên mã)
            "iPhone 16", "iPhone 16 Plus", "iPhone 16 Pro", "iPhone 16 Pro Max"
        ]

        
        return supportedModels.contains(modelName)
    }

    private func is3DCameraError() -> Bool {
        // Attempt to get the TrueDepth front camera (camera with depth capabilities)
        guard let camera = AVCaptureDevice.default(.builtInTrueDepthCamera, for: .video, position: .front) else {
            print("3D camera (TrueDepth) not available on this device.")
            return true
        }

        // Check if the camera supports focus, torch, or is in a valid format
        if !camera.isConnected {
            print("3D camera (TrueDepth) has unsupported configurations.")
            return true
        }

        // Try to lock the device for configuration and check for errors
        do {
            try camera.lockForConfiguration()
            camera.unlockForConfiguration()
            print("3D camera (TrueDepth) available and configured correctly.")
            return false // No error
        } catch {
            print("Error accessing 3D camera: \(error)")
            return true // Camera error
        }
    }
}

extension UIDevice {
    var modelName: String {
        var systemInfo = utsname()
        uname(&systemInfo)
        let machineMirror = Mirror(reflecting: systemInfo.machine)
        let identifier = machineMirror.children.compactMap { element in
            guard let value = element.value as? Int8, value != 0 else { return nil }
            return String(UnicodeScalar(UInt8(value)))
        }.joined()

        return mapToDevice(identifier: identifier)
    }

    private func mapToDevice(identifier: String) -> String {
        switch identifier {
        case "iPhone10,3", "iPhone10,6": return "iPhone X"
        case "iPhone11,2": return "iPhone XS"
        case "iPhone11,4", "iPhone11,6": return "iPhone XS Max"
        case "iPhone11,8": return "iPhone XR"
        case "iPhone12,1": return "iPhone 11"
        case "iPhone12,3": return "iPhone 11 Pro"
        case "iPhone12,5": return "iPhone 11 Pro Max"
        case "iPhone13,1": return "iPhone 12 mini"
        case "iPhone13,2": return "iPhone 12"
        case "iPhone13,3": return "iPhone 12 Pro"
        case "iPhone13,4": return "iPhone 12 Pro Max"
        case "iPhone14,4": return "iPhone 13 mini"
        case "iPhone14,5": return "iPhone 13"
        case "iPhone14,2": return "iPhone 13 Pro"
        case "iPhone14,3": return "iPhone 13 Pro Max"
        case "iPhone15,2": return "iPhone 14 Pro"
        case "iPhone15,3": return "iPhone 14 Pro Max"
        case "iPhone14,6": return "iPhone SE (3rd generation)"
        case "iPhone15,4": return "iPhone 14"
        case "iPhone15,5": return "iPhone 14 Plus"
        case "iPhone16,1": return "iPhone 15"
        case "iPhone16,2": return "iPhone 15 Plus"
        case "iPhone16,3": return "iPhone 15 Pro"
        case "iPhone16,4": return "iPhone 15 Pro Max"
        case "iPhone17,1": return "iPhone 16"
        case "iPhone17,2": return "iPhone 16 Plus"
        case "iPhone17,3": return "iPhone 16 Pro"
        case "iPhone17,4": return "iPhone 16 Pro Max"
        default: return identifier
        }
    }
}

