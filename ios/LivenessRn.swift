import React
import UIKit
@_implementationOnly import LivenessUtility
//import LivenessUtility


@available(iOS 13.0, *)
@objc(LivenessRn)
class LivenessRn: NSObject, LivenessUtilityDetectorDelegate {
     var appId = ""
  var secret = "ABCDEFGHIJKLMNOP"
  var baseURL = "https://face-matching.vietplus.eu"
  var clientTransactionId = "TEST"

  var transactionId = ""
  var livenessDetector: LivenessUtilityDetector?
  @IBOutlet weak var previewView: UIView!
     
  var _callback: RCTResponseSenderBlock? = nil
  
  @objc(configure:secret:baseURL:clientTransactionId:)
  func configure(appId: String, secret: String? = nil, baseURL: String? = nil, clientTransactionId: String? = "") {
    self.appId = appId
    if (secret != nil && secret != "") {
      self.secret = secret!
    }
    if (baseURL != nil && baseURL != "") {
      self.baseURL = baseURL!
    }
    if (clientTransactionId != nil && clientTransactionId != "") {
      self.clientTransactionId = clientTransactionId!
    }
//    let urlPrivate = Bundle.main.url(forResource: "com.qts.test", withExtension: "txt")
//    let urlPublic = Bundle.main.url(forResource: "eid", withExtension: "txt")
    let privateKey = "-----BEGIN PRIVATE KEY-----\n" +
      "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQC7K1QJ6YLRMoNv\n" +
      "mqV3qkNXJZWxCT95+/xVlKhea/i3QpJk5Ier4hgUqn3MmlU3NXRv6eHZh579uzqM\n" +
      "1U8PJzMGj2j9XnlOkQtWImSKiMUPQYJ4CSnUGCLpiMomZKjGO4tLiS/yCSJ/BFrI\n" +
      "TBGYJhJL9wOYYzZdY/mNWSr1+ZsqQFH0Ug7n4XfYrJD7XgtozIgiY5LtbdoV/6At\n" +
      "TN8H8czzQnLaA8NjDb8npP7ogu99KVivtoAJJhLZJ9nvXyP9Dp7sKi9d33LaxFwH\n" +
      "OiNWRm3qFGm1G8RZoWg4G1h+ETL0z4COBgvAhToGI5gkkeyw+qFfS9hqLN0hJWAm\n" +
      "o41oGPo1AgMBAAECggEBAI1qzmNy4JmJjg+MDAufRKQazMBnmWNkhiJvYMt+zvxA\n" +
      "O3YpyWyQNtueedBWp55AMErCrxd5xiI2DaYNIV/0oTQKtSwC7qrzIlqhP9AASMwf\n" +
      "FiH14nnTBsXmyb46fd7RbIzVCbnZNww7URBXkU+hLF/jMf84rwHfINWwkqopPxir\n" +
      "F5Ohqt1G/PxzI3/rc20DzDJX331em5qHBqACp1JcHXtpaFKBOJihVnhYqxon9k1o\n" +
      "qcR79HNRlIwHWsxsOUEM8zPTbstQaqMgKLFXyENM43C+B/f+Oz2DBdF32RD7jq8Q\n" +
      "xLR1gidq+KCXEejOBuRexrrT4fQiCb7e2robh8o/IUECgYEA4XubVcqjmZmhIlt9\n" +
      "PU+63IC1dEVc40PZtJ5AiQvZa+zLCl9ik+9k/dmJE5WUZDki76W3OB+kJq3fUWgQ\n" +
      "tyo0UkpxHwqryefGg09syu5cNGE/zd7ZREF0aIsHnXaPtroKq8Z6mz4FctLt9egr\n" +
      "8V0M670N9rQz996+E/KHf4jEeBECgYEA1IBBZNWJDE7lIip8CobnPVh718p2HTuc\n" +
      "lxeTFrRgI3wWnitYhCGLnJMGDvNv/znApsB7aAgVFz3r5jGKxTPPCwa9gwrKXoJy\n" +
      "vBWRIL2gajImGU5fOoDQZJ3dGNgNh8anoe0/esMbdIZMFY6rAIWGiE+Y17+Or1UL\n" +
      "EBUen8o7Y+UCgYBWn17QeZWaF5wAj/cwC6Y0ubl73n3NzS4gpj8Spxuyy3hBFt3P\n" +
      "CUPaBa0Uef1U92JFgHs/s2Ajf95v7rOlOjB5gKGulDHk0gbAQU4BM8r2UHnrg/Yh\n" +
      "s6ed1fNp+bdCMnyQ+yH068G6F/BU7Qmcouuo0KtBoH7qdYa+MQj+5LLdkQKBgEdJ\n" +
      "56ZORLXOWexGWGqnqzfXUWSpVUqlTvkZPY0mYgJFhMj3PbDGGDIk2Kl3XaE/3LOU\n" +
      "a1IRNBIiAdutzyItKU5HqpglrJJcLOWQTqmvM/usaz+eHTBhOogmtZ+6C3/7Uw1t\n" +
      "rBghEDrdOvUYcaGxKdrc6Sen6dREMXvpueZdT+NJAoGAIsPaK0Rgu6Z540hiCF2M\n" +
      "0yYHriXljTAWtdm5FpCfoLwKox1OYLMQlFIXfN1qqmo6m13O+MW3IIU7X/aAk7T6\n" +
      "UW7GZybBe40J2AxVC48GX+jVk5iQjBzUtEf81jIZp61AD5KijNn33lHf653K09ch\n" +
      "uw+D9R3JrjzTHoyep6eif/s=\n" +
      "-----END PRIVATE KEY-----\n"
    let publicKey = "-----BEGIN CERTIFICATE-----\n" +
      "MIIDjzCCAnegAwIBAgIEPhgWFTANBgkqhkiG9w0BAQsFADBbMScwJQYDVQQDDB5SZWdlcnkgU2Vs\n" +
      "Zi1TaWduZWQgQ2VydGlmaWNhdGUxIzAhBgNVBAoMGlJlZ2VyeSwgaHR0cHM6Ly9yZWdlcnkuY29t\n" +
      "MQswCQYDVQQGEwJVQTAgFw0yNDA0MTEwMDAwMDBaGA8yMTI0MDQxMTAzMTMwOVowUTEdMBsGA1UE\n" +
      "AwwUcXVhbmd0cnVuZ3F0cy5jb20udm4xIzAhBgNVBAoMGlJlZ2VyeSwgaHR0cHM6Ly9yZWdlcnku\n" +
      "Y29tMQswCQYDVQQGEwJVQTCCASIwDQYJKoZIhvcNAQEBBQADggEPADCCAQoCggEBAIfK7LjzjqCo\n" +
      "VSzXz/ROHc2IyBMc89GwnR0slF1Lenavs+r+lnjFAxkVonBRTjtMj1pWqlACnd3qiIAD/8GbSagG\n" +
      "qsV43BDPbioDibWg/9wln82VLwEQohjLTl7VJtKuRAIUcg2nY4r5LNzpdClJx+k7zrIVDKSO8tRa\n" +
      "onU1dU6KLSmC2ZOzT10zrK4qmjvN/LFp0rlXJtdw++MUOIM9kccyi+3MK7iiraNV7Tlazy9xF0OZ\n" +
      "ytzgSX5R+oHE3aUS0M+W4p/dhihvLKjiejuw46E0dqEKxaqMJHXj2Qei1Ky1RrdRBNB0oQLCoUGx\n" +
      "KRaYw1CbZ7QWAgnrbqTvs1Y8pwUCAwEAAaNjMGEwDwYDVR0TAQH/BAUwAwEB/zAOBgNVHQ8BAf8E\n" +
      "BAMCAYYwHQYDVR0OBBYEFIlsqZHH0jmPvIjlF4YARXnamm7AMB8GA1UdIwQYMBaAFIlsqZHH0jmP\n" +
      "vIjlF4YARXnamm7AMA0GCSqGSIb3DQEBCwUAA4IBAQBfSk1XtHU8ix87g+lVzRQrEf7qsqWiwkN9\n" +
      "TW05qaPDMoMEoe/MW0YJZ+QwgvGMNLkEWjz/v0p1fVFF6kIolbo1o+1P6D4RCWvyB8S5zV9Mv+aR\n" +
      "1uWbAYiAA2uql/NrIJ3V1pJhIgRgDsRNuVP8MhNZc6DgJQLZOMKLwXsNHDtGOHk+ZcPiyWcjb4a3\n" +
      "voZCp4HN8+V2umO+QGuESZhTLihBnXv9HTpKxwWu4tK/4dgngDYM3UmChRjD/H7A3aYV4Xyxkqw2\n" +
      "rnd2LAr/zUEhFkbs21iG3DF0cHGKI15YzIq5pEhb9l4ePcCIgWgnJDNJPA/QhxpRB1XhP4bpK8kP\n" +
      "GJ8f\n" +
      "-----END CERTIFICATE-----"
    Networking.shared.setup(appId: appId, logLevel: .debug, url: self.baseURL, publicKey: publicKey, privateKey: privateKey)
    print("setup SS")
  }
  
  @objc(getDeviceId:)
  func getDeviceId(callback: RCTResponseSenderBlock? = nil) {
    DispatchQueue.main.sync {
      print("privateKey")
      let resposne = Networking.shared.generateDeviceInfor()
      print(resposne)
      callback?([resposne])
    }
  }
  
  @objc(initTransaction:callback:)
  func initTransaction(callback: RCTResponseSenderBlock? = nil) async {
    do {
      let response = try await Networking.shared.initTransaction()
      callback?([response])
    } catch {
      callback?([error])
    }
  }
  
  @objc(registerFace:)
  func registerFace(callback: RCTResponseSenderBlock? = nil) {
    Task{
      do{
        try await Networking.shared.registerFace(faceImage: UIImage(named: "image.png")!)
      }catch{
      }
    }
  }
  
  @objc(startLiveNess:)
  func startLiveNess(callback: RCTResponseSenderBlock? = nil) {
    self._callback = callback
    Task {
      do {
        print("response==0")
        let response = try await Networking.shared.initTransaction()
        print("response==1")
        print(response)
        print("response==2")
        if response.status == 200 {
          self.transactionId = response.data
          self.livenessDetector = LivenessUtil.createLivenessDetector(previewView: self.previewView, threshold: .low,delay: 0, smallFaceThreshold: 0.25, debugging: true, delegate: self, livenessMode: .twoDimension)
          try self.livenessDetector?.getVerificationRequiresAndStartSession(transactionId: self.transactionId)
        }
      } catch {
        print("response==4")
        print(error)
        print("response==5")
        self._callback?([NSNull(), error])
      }
    }
  }
  
  @objc(stopLiveness)
  func stopLiveness() {
    self.livenessDetector?.stopLiveness()
  }
  
  func liveness(liveness: LivenessUtilityDetector, didFail withError: LivenessError) {
    self._callback?([withError])
  }
  func liveness(liveness: LivenessUtilityDetector, didFinish verificationImage: UIImage, livenesScore: Float, faceMatchingScore: Float, result: Bool, message: String, videoURL: URL?) {
    self._callback?([message])
  }
}
