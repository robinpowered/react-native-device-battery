#import <Foundation/Foundation.h>

#if __has_include(<React/RCTAssert.h>)
#import <React/RCTBridgeModule.h>
#import <React/RCTEventEmitter.h>
#else // backward compat for rn pre 0.40
#import "RCTBridgeModule.h"
#import "RCTEventEmitter.h"
#endif

@interface DeviceBattery : RCTEventEmitter <RCTBridgeModule>

@end
