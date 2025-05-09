//
//  UINavigationBarAppearance.swift
//  QTSIdentityApp
//
//  Created by Nguyễn Thanh Bình on 11/12/24.
//

import UIKit

extension UINavigationBarAppearance {
    static func defaultAppearance() -> UINavigationBarAppearance {
        let textAttributes = [NSAttributedString.Key.foregroundColor: UIColor.black, .font: UIFont.systemFont(ofSize: 17, weight: .semibold)]
        let appearance = UINavigationBarAppearance()
        appearance.configureWithOpaqueBackground()
        appearance.titleTextAttributes = textAttributes
//            appearance.backgroundColor = UIColor(r: 240, g: 240, b: 242) // UIColor(red: 0.0/255.0, green: 125/255.0, blue: 0.0/255.0, alpha: 1.0)
        appearance.backgroundColor = UIColor.white
        appearance.shadowColor = .clear  //removing navigationbar 1 px bottom border.
        let paragraphStyle = NSMutableParagraphStyle()
        paragraphStyle.lineBreakMode = .byWordWrapping
        appearance.largeTitleTextAttributes = [
          .foregroundColor: UIColor.black,
            .font: UIFont.systemFont(ofSize: 24, weight: .semibold),
            .paragraphStyle: paragraphStyle
        ]
        return appearance
    }
}
