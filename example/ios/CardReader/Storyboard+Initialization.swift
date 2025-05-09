//
//  Storyboard+Initialization.swift
//  iOS-Starter-Kit
//
//  Created by Nghia Tran on 3/20/17.
//  Copyright © 2017 nghiatran. All rights reserved.
//

import Foundation
import UIKit

//
// MARK: - StoryboardInitialization
protocol StoryboardInitialization: Identifier {
    
    // Associated type: Must adopt Identifier
    associatedtype Element: Identifier
    
    static var storyBoard: AppStoryboard {get set}
    
    // Get view controller from storyboard
    static var storyboardViewController: Element {get}
}

//
// MARK: - Extend StoryboardInitialization if Element is UIViewController
extension StoryboardInitialization where Element: UIViewController {
    
    // Get view controller
    static var storyboardViewController: Element {
        return storyBoard.instance.instantiateViewController(withIdentifier: Element.identifier) as! Element
    }
}

enum AppStoryboard : String {
    case Registration     = "Registration"
    var instance : UIStoryboard {
        return UIStoryboard(name: self.rawValue, bundle: nil)
    }
}
