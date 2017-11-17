//
//  DeviceBattery.m
//  DeviceBattery
//
//  Created by Atticus White on 12/1/15.
//  Copyright © 2015 Atticus White. All rights reserved.
//
//  2017/02/08 @tsella
//  - fix duplicate includes
//  - use EventEmitter instead of deprecated EventDispatcher

#import "DeviceBattery.h"

@implementation DeviceBattery

static const NSString *BATTERY_CHANGE_EVENT = @"batteryChanged";

- (instancetype)init
{
    if((self = [super init])) {
        dispatch_async(dispatch_get_main_queue(), ^{
            [[UIDevice currentDevice] setBatteryMonitoringEnabled:YES];
        });
        
        [[NSNotificationCenter defaultCenter] addObserver:self
                                                 selector:@selector(batteryLevelChanged:)
                                                     name:UIDeviceBatteryLevelDidChangeNotification
                                                   object:nil];
        [[NSNotificationCenter defaultCenter] addObserver:self
                                                 selector:@selector(batteryLevelChanged:)
                                                     name:UIDeviceBatteryStateDidChangeNotification
                                                   object:nil];
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

- (NSArray<NSString *> *)supportedEvents {
    return @[BATTERY_CHANGE_EVENT];
}

-(void)batteryLevelChanged:(NSNotification*)notification {
    UIDeviceBatteryState batteryState = [UIDevice currentDevice].batteryState;
    NSMutableDictionary* payload = [NSMutableDictionary dictionaryWithCapacity:2];
    bool isCharging = batteryState == UIDeviceBatteryStateCharging;
    float batteryLevel = [UIDevice currentDevice].batteryLevel;
    
    [payload setObject:[NSNumber numberWithBool:isCharging] forKey:@"charging"];
    [payload setObject:[NSNumber numberWithFloat:batteryLevel] forKey:@"level"];

    [self sendEventWithName:BATTERY_CHANGE_EVENT body:payload];
}

- (void)dealloc
{
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}

@end
