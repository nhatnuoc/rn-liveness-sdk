#import <Foundation/Foundation.h>
#import <React/RCTBridgeModule.h>
#import <React/RCTEventEmitter.h>

@interface RCT_EXTERN_MODULE(LivenessRn, NSObject)

RCT_EXTERN_METHOD(configure:(NSString *)appId secret:(NSString *)secret baseURL:(NSString *)baseURL clientTransactionId:(NSString *)clientTransactionId)

RCT_EXTERN_METHOD(registerFace:(NSString *)image
                 withCallback:(RCTResponseSenderBlock)callback)
RCT_EXTERN_METHOD(initTransaction: (RCTResponseSenderBlock)callback)
RCT_EXTERN_METHOD(stopLiveness:)
RCT_EXTERN_METHOD(getDeviceId: (RCTResponseSenderBlock)callback)

RCT_EXTERN_METHOD(initTransaction:(RCTPromiseResolveBlock)resolve withRejecter:(RCTPromiseRejectBlock)reject)

@end
