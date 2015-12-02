# react-native-device-battery

Get and observe the devices battery level and charging status


## Installation

### iOS
TBD

### Android
TBD

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
