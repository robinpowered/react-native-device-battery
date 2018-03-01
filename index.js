var {NativeModules, NativeEventEmitter} = require('react-native');
var {DeviceBattery} = NativeModules;

const batteryEventEmitter = new NativeEventEmitter(DeviceBattery);

export default {
  isCharging: DeviceBattery.isCharging,
  getBatteryLevel: DeviceBattery.getBatteryLevel,
  addListener: callback => batteryEventEmitter.addListener(DeviceBattery.BATTERY_CHANGE_EVENT, callback),
  removeListener: callback => batteryEventEmitter.removeListener(DeviceBattery.BATTERY_CHANGE_EVENT, callback)
};
