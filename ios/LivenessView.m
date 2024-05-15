//
//  LivenessView.m
//  AppTest
//
//  Created by NamNg on 5/14/24.
//

#import <Foundation/Foundation.h>
#import <React/RCTBridgeModule.h>
#import <React/RCTEventEmitter.h>
#import <React/RCTViewManager.h>

 
@interface RCT_EXTERN_MODULE(RCTLivenessViewManager, RCTViewManager)
  RCT_EXPORT_VIEW_PROPERTY(onEvent, RCTBubblingEventBlock)
@end
