//
//  DeviceBattery.h
//  DeviceBattery
//
//  Created by Atticus White on 12/1/15.
//  Copyright © 2015 Atticus White. All rights reserved.
//
//  2017/02/08 @tsella
//  - support rn 0.40+
//  - use EventEmitter instead of deprecated EventDispatcher

#import <Foundation/Foundation.h>

#if __has_include(<React/RCTAssert.h>)
#import <React/RCTBridgeModule.h>
#import <React/RCTBridge.h>
#import <React/RCTEventEmitter.h>
#else // backward compat for rn pre 0.40
#import "RCTBridgeModule.h"
#import "RCTBridge.h"
#import "RCTEventEmitter.h"
#endif

@interface DeviceBattery : RCTEventEmitter <RCTBridgeModule>

@end
