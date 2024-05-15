//
//  RCTLivenessViewManager.swift
//  AppTest
//
//  Created by NamNg on 5/14/24.
//

import Foundation
import React


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
