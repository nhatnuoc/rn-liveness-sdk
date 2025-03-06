#import <Foundation/Foundation.h>
#import <React/RCTBridgeModule.h>
#import <React/RCTEventEmitter.h>

@interface RCT_EXTERN_MODULE(LivenessRn, NSObject)

RCT_EXTERN_METHOD(setupLiveness:(NSString *)appId baseURL:(NSString *)baseURL publicKey:(NSString *)publicKey privateKey:(NSString *)privateKey)

RCT_EXTERN_METHOD(registerFace:(NSString *)imageUri
                 resolve:(RCTPromiseResolveBlock)resolve
                  reject:(RCTPromiseRejectBlock)reject)

@end
