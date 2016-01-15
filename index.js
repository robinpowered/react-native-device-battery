var { NativeModules, DeviceEventEmitter } = require('react-native');
var {DeviceBattery} = NativeModules;

export default {
  isCharging: DeviceBattery.isCharging,
  getBatteryLevel: DeviceBattery.getBatteryLevel,
  addListener: (callback) => {
    return DeviceEventEmitter.addListener('batteryChange', callback);
  },
  removeListener: (listener) => {
    // DeviceEventEmitter.removeListener('batteryChange', listener);
  }
};
