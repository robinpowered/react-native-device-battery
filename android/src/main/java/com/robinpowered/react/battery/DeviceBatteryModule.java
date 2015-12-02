package com.robinpowered.react.battery;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.BroadcastReceiver;
import android.os.BatteryManager;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.WritableNativeMap;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.Promise;
import com.facebook.react.modules.core.DeviceEventManagerModule;

// @link http://developer.android.com/training/monitoring-device-state/battery-monitoring.html
public class DeviceBatteryModule extends ReactContextBaseJavaModule {
  public static final String EVENT_NAME = "batteryChange";
  public static final String IS_CHARGING_KEY = "charging";
  public static final String BATTERY_LEVEL_KEY = "level";

  private ReactApplicationContext reactApplicationContext;
  private Activity activity = null;
  private Intent batteryStatus = null;
  private BroadcastReceiver batteryStateReceiver;


  public DeviceBatteryModule(ReactApplicationContext reactApplicationContext, Activity activity) {
    super(reactApplicationContext);
    this.reactApplicationContext = reactApplicationContext;
    this.activity = activity;
    this.registerBatteryStateReceiver();
  }

  private float getBatteryPrecentageFromIntent(Intent intent) {
    int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
    int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
    return level / (float) scale;
  }

  private boolean getIsChangingFromIntent(Intent intent) {
    int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
    return (
      status == BatteryManager.BATTERY_STATUS_CHARGING ||
      status == BatteryManager.BATTERY_STATUS_FULL
    );
  }

  private WritableNativeMap getJSMap (Intent intent) {
    float batteryPercentage = getBatteryPrecentageFromIntent(intent);
    boolean isCharging = getIsChangingFromIntent(intent);
    System.out.println("BATTERY LEVEL CHANGE DETECTED");
    WritableNativeMap params = new WritableNativeMap();
    params.putBoolean(IS_CHARGING_KEY, isCharging);
    params.putDouble(BATTERY_LEVEL_KEY, (double) batteryPercentage);
    return params;
  }

  private void registerBatteryStateReceiver() {
    batteryStateReceiver = new BroadcastReceiver() {
      @Override
      public void onReceive(Context context, Intent intent) {
        // only emit an event if the Catalyst instance is avialable
        if (reactApplicationContext.hasActiveCatalystInstance()) {
          WritableNativeMap params = getJSMap(intent);
          reactApplicationContext
            .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
            .emit(EVENT_NAME, params);
        }
      }
    };
    IntentFilter batteryFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
    batteryStatus = this.activity.registerReceiver(batteryStateReceiver, batteryFilter);
  }

  @ReactMethod
  public void getBatteryLevel(Promise promise) {
    if (batteryStatus != null) {
      float batteryPercentage = getBatteryPrecentageFromIntent(batteryStatus);
      promise.resolve((double) batteryPercentage);
    } else {
      promise.reject("Battery manager is not active");
    }
  }

  @ReactMethod
  public void isCharging(Promise promise) {
    if (batteryStatus != null) {
      boolean isCharging = getIsChangingFromIntent(batteryStatus);
      promise.resolve(isCharging);
    } else {
      promise.reject("Battery manager is not active");
    }
  }

  @Override
  public String getName() {
    return "DeviceBattery";
  }
}
