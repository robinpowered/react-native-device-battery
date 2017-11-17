var { NativeModules, NativeEventEmitter } = require('react-native');
var { DeviceBattery } = NativeModules;

export default {
  isCharging: DeviceBattery.isCharging,
  getBatteryLevel: DeviceBattery.getBatteryLevel,
  addListener: (callback) => {
    const deviceBatteryEvt = new NativeEventEmitter(NativeModules.DeviceBattery);
    return deviceBatteryEvt.addListener('batteryChange', callback);
  },
  removeListener: (listener) => {
    // FIXME: missing from original implementation
    // DeviceEventEmitter.removeListener('batteryChange', listener);
  }
};
