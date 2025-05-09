//
//  CardReaderRn.m
//  LivenessRnExample
//
//  Created by Nguyễn Thanh Bình on 8/5/25.
//

#import <Foundation/Foundation.h>
#import <React/RCTBridgeModule.h>
#import <React/RCTEventEmitter.h>

@interface RCT_EXTERN_MODULE(CardReaderRn, NSObject)

RCT_EXTERN_METHOD(readCard:(NSString *)appId baseURL:(NSString *)baseURL publicKey:(NSString *)publicKey privateKey:(NSString *)privateKey resolve:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject)

@end
