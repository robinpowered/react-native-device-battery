package com.robinpowered.react.battery;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.BroadcastReceiver;
import android.os.BatteryManager;
import android.util.Log;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeMap;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.Promise;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.facebook.react.modules.core.RCTNativeAppEventEmitter;
import com.facebook.react.bridge.LifecycleEventListener;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

// @link http://developer.android.com/training/monitoring-device-state/battery-monitoring.html
public class DeviceBatteryModule extends ReactContextBaseJavaModule
  implements LifecycleEventListener {

  public static final String EVENT_NAME = "batteryChange";
  public static final String IS_CHARGING_KEY = "charging";
  public static final String BATTERY_LEVEL_KEY = "level";

  private Intent batteryStatus = null;
  private @Nullable PowerConnectionReceiver batteryStateReceiver;


  public DeviceBatteryModule(ReactApplicationContext reactApplicationContext) {
    super(reactApplicationContext);
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
    WritableNativeMap params = new WritableNativeMap();
    params.putBoolean(IS_CHARGING_KEY, isCharging);
    params.putDouble(BATTERY_LEVEL_KEY, (double) batteryPercentage);
    return params;
  }

  public void notifyBatteryStateChanged(Intent intent) {
    batteryStatus = intent;
    // only emit an event if the Catalyst instance is avialable
    if (getReactApplicationContext().hasActiveCatalystInstance()) {
      WritableNativeMap params = getJSMap(intent);
      try {
        getReactApplicationContext()
                .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                .emit(EVENT_NAME, params);
      } catch (Exception e) {
        Log.e(getName(), "notifyBatteryStateChanged called before bundle loaded");
      }
    }
  }

  @Override
  public Map<String, Object> getConstants() {
    final Map<String, Object> constants = new HashMap<>();
    constants.put("BATTERY_CHANGE_EVENT", EVENT_NAME);
    return constants;
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

  private void maybeRegisterReceiver() {
    if (batteryStateReceiver != null) {
      return;
    }
    batteryStateReceiver = new PowerConnectionReceiver();
    IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
    batteryStatus = getReactApplicationContext().registerReceiver(batteryStateReceiver, filter);
  }

  private void maybeUnregisterReceiver() {
    if (batteryStateReceiver == null) {
      return;
    }
    getReactApplicationContext().unregisterReceiver(batteryStateReceiver);
    batteryStateReceiver = null;
    batteryStatus = null;
  }

  @Override
  public String getName() {
    return "DeviceBattery";
  }

  @Override
  public void initialize() {
    getReactApplicationContext().addLifecycleEventListener(this);
    maybeRegisterReceiver();
  }

  @Override
  public void onHostResume() {
    maybeRegisterReceiver();
  }
  @Override
  public void onHostPause() {
    maybeUnregisterReceiver();
    }
  @Override
  public void onHostDestroy() {
    maybeUnregisterReceiver();
  }

  class PowerConnectionReceiver extends BroadcastReceiver {
    private DeviceBatteryModule mBatteryModule;

    @Override
    public void onReceive(Context context, Intent intent) {
      notifyBatteryStateChanged(intent);
    }
  }

}
