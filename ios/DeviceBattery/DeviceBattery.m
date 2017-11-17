#import "DeviceBattery.h"

@implementation DeviceBattery
RCT_EXPORT_MODULE();

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

- (void)dealloc
{
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}

- (NSArray<NSString *> *)supportedEvents {
    return @[BATTERY_CHANGE_EVENT];
}

// UIDevice (UIKit) may only be accessed on main thread
+(BOOL)requiresMainQueueSetup
{
    return YES;
}

RCT_REMAP_METHOD(isCharging,
                 resolver:(RCTPromiseResolveBlock)resolve
                 rejecter:(RCTPromiseRejectBlock)reject) {
    UIDeviceBatteryState batteryState = [UIDevice currentDevice].batteryState;
    if (batteryState == UIDeviceBatteryStateCharging) {
        resolve(@YES);
    } else {
        resolve(@NO);
    }
}

RCT_REMAP_METHOD(getBatteryLevel,
                 resolver:(RCTPromiseResolveBlock)resolve
                 rejecter:(RCTPromiseRejectBlock)reject)
{
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

    [self sendEventWithName:BATTERY_CHANGE_EVENT body:payload];
}

@end
