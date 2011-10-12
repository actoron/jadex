package jadex.android.bluetooth.device.factory;

import jadex.android.bluetooth.device.IBluetoothDevice;

public interface IBluetoothDeviceFactory {
	IBluetoothDevice createBluetoothDevice(String address);
}