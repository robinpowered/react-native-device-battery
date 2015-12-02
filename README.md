# react-native-device-battery

Get and observe the devices battery level and charging status


## Installation

Install the node module
```
npm install react-native-device-battery --save
```

### iOS
TBD

### Android
Add the following to `android/settings.grade`
```
include ':react-native-device-battery'
project(':react-native-device-battery').projectDir = new File(settingsDir, '../node_modules/react-native-device-battery/android')
```

Add the following to `android/app/build.gradle`
```
compile project(':react-native-device-battery')
```

Register the module in `MainActivity.java`
```java
import com.robinpowered.react.battery.DeviceBatteryPackage;  // <--- import

public class MainActivity extends Activity implements DefaultHardwareBackBtnHandler {
  ......

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mReactRootView = new ReactRootView(this);

    mReactInstanceManager = ReactInstanceManager.builder()
      .setApplication(getApplication())
      .setBundleAssetName("index.android.bundle")
      .setJSMainModuleName("index.android")
      .addPackage(new MainReactPackage())
      .addPackage(new DeviceBatteryPackage()) // <------ add this line to yout MainActivity class
      .setUseDeveloperSupport(BuildConfig.DEBUG)
      .setInitialLifecycleState(LifecycleState.RESUMED)
      .build();

    mReactRootView.startReactApplication(mReactInstanceManager, "AndroidRNSample", null);

    setContentView(mReactRootView);
  }

  ......

}
```


## Example Usage
```js
import DeviceBattery from 'react-native-device-battery';

// get the battery level
DeviceBattery.getBatteryLevel().then(level => {
  console.log(level); // between 0 and 1
});

// check if the device is charging
DeviceBattery.isCharging().then(isCharging => {
  console.log(isCharging) // true or false
});

// as a listener
var onBatteryStateChanged = (state) => {
  console.log(state) // {level: 0.95, charging: true}
};

// to attach a listener
DeviceBattery.addListener(onBatteryStateChanged);

// to remove a listener
DeviceBattery.removeListener(onBatteryStateChanged);
```
