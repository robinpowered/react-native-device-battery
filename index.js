var { NativeModules, RCTDeviceEventEmitter } = require('react-native');
var {DeviceBattery} = NativeModules;

export default {
  isCharging: DeviceBattery.isCharging,
  getBatteryLevel: DeviceBattery.getBatteryLevel,
  addListener: (callback) => {
    return RTCDeviceEventEmitter.addListener('batteryChange', callback);
  },
  removeListener: (listener) => {
    RTCDeviceEventEmitter.removeListener('batteryChange', listener);
  }
};
