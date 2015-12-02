//
//  DeviceBattery.m
//  DeviceBattery
//
//  Created by Atticus White on 12/1/15.
//  Copyright © 2015 Atticus White. All rights reserved.
//

#import "DeviceBattery.h"
#import "RCTBridge.h"
#import "RCTEventDispatcher.h"

@implementation DeviceBattery
@synthesize bridge = _bridge;

- (instancetype)init
{
    if((self = [super init])) {
        [[UIDevice currentDevice] setBatteryMonitoringEnabled:YES];
        
        [[NSNotificationCenter defaultCenter] addObserver:self
                                                 selector:@selector(batteryLevelChanged:)
                                                     name:UIDeviceBatteryLevelDidChangeNotification
                                                   object:nil];
        [[NSNotificationCenter defaultCenter] addObserver:self
                                                 selector:@selector(batteryLevelChanged:)
                                                     name:UIDeviceBatteryStateDidChangeNotification
                                                   object: nil];
    }
    return self;
}


RCT_EXPORT_MODULE();

RCT_REMAP_METHOD(isCharging,
                 isChargingResolver:(RCTPromiseResolveBlock)resolve
                 isChargingRejector:(RCTPromiseRejectBlock)reject) {
    UIDeviceBatteryState batteryState = [UIDevice currentDevice].batteryState;
    if (batteryState == UIDeviceBatteryStateCharging) {
        resolve(@YES);
    } else {
        resolve(@NO);
    }
}

RCT_REMAP_METHOD(getBatteryLevel,
                 batteryLevelResolver:(RCTPromiseResolveBlock)resolve
                 batteryLevelRejector:(RCTPromiseRejectBlock)reject) {
    float batteryLevel = [UIDevice currentDevice].batteryLevel;
    resolve(@(batteryLevel));
}

-(void)batteryLevelChanged:(NSNotification*)notification {
    UIDeviceBatteryState batteryState = [UIDevice currentDevice].batteryState;
    NSMutableDictionary* payload = [NSMutableDictionary dictionaryWithCapacity:2];
    bool isCharging = batteryState == UIDeviceBatteryStateCharging;
    float batteryLevel = [UIDevice currentDevice].batteryLevel;
    
    [payload setObject:[NSNumber numberWithBool:isCharging] forKey:@"charging"];
    [payload setObject:[NSNumber numberWithFloat:batteryLevel] forKey:@"level"];
    [self.bridge.eventDispatcher sendDeviceEventWithName:@"batteryChange" body:payload];
}

- (void)dealloc
{
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}

@end
