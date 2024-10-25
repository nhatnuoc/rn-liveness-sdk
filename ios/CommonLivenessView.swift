import Foundation
import React
import UIKit

@available(iOS 15.0, *)
class CommonLivenessView: UIView {

    var requestid = ""
    var appId = ""
    var baseUrl = ""
    var privateKey = ""
    var publicKey = ""
    var secret = "ABCDEFGHIJKLMNOP"
    private var livenessView: UIView? // Dùng một UIView chung cho cả hai loại
    var debugging = false {
        didSet {
            setupView() // Gọi hàm khi debugging thay đổi
        }
    }
    
    override init(frame: CGRect) {
        super.init(frame: frame)
        setupView() // Khởi tạo view
    }

    required init?(coder: NSCoder) {
        super.init(coder: coder)
        setupView() // Khởi tạo view
    }

    private func setupView() {
        // Xóa tất cả subviews trước khi thêm mới
        livenessView?.removeFromSuperview()

        // Thêm LivenessView hoặc Liveness3DView tùy thuộc vào giá trị debugging
        if debugging {
            livenessView = LivenessView(frame: bounds)
        } else {
//            livenessView = Liveness3DView(frame: bounds)
        }

        // Thiết lập autoresizing mask để đảm bảo view có thể thay đổi kích thước
        livenessView?.autoresizingMask = [.flexibleWidth, .flexibleHeight]

        // Thêm view mới vào màn hình
        if let livenessView = livenessView {
            addSubview(livenessView)
        }
    }

    @objc func setDebugging(_ val: Bool) {
        print("Debugging set to: \(val)")
        self.debugging = val // Cập nhật thuộc tính và gọi setupView
    }

    private func pushEvent(data: Any) -> Void {
    if (self.onEvent != nil) {
      let event = ["data": data]
      self.onEvent!(event)
    }
  }
  
  @objc var onEvent: RCTBubblingEventBlock?
    
    @objc func setRequestid(_ val: NSString) {
      self.requestid = val as String
    }
      
    @objc func setAppId(_ val: NSString) {
      self.appId = val as String
    }
      
    @objc func setBaseUrl(_ val: NSString) {
      self.baseUrl = val as String
    }
      
    @objc func setPrivateKey(_ val: NSString) {
      self.privateKey = val as String
    }
      
    @objc func setPublicKey(_ val: NSString) {
      self.publicKey = val as String
    }
}
