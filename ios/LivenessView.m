#import <Foundation/Foundation.h>
#import <React/RCTBridgeModule.h>
#import <React/RCTEventEmitter.h>
#import <React/RCTViewManager.h>
 
@interface RCT_EXTERN_MODULE(RCTLivenessViewManager, RCTViewManager)
  RCT_EXPORT_VIEW_PROPERTY(onSuccess, RCTBubblingEventBlock)
  RCT_EXPORT_VIEW_PROPERTY(onFailure, RCTBubblingEventBlock)
  RCT_EXPORT_VIEW_PROPERTY(idCardRequestId, NSString)
  RCT_EXPORT_VIEW_PROPERTY(debugging, BOOL)
  RCT_EXPORT_VIEW_PROPERTY(useFlash, BOOL)
  RCT_EXPORT_VIEW_PROPERTY(appId, NSString)
  RCT_EXPORT_VIEW_PROPERTY(baseURL, NSString)
  RCT_EXPORT_VIEW_PROPERTY(publicKey, NSString)
  RCT_EXPORT_VIEW_PROPERTY(privateKey, NSString)
@end
