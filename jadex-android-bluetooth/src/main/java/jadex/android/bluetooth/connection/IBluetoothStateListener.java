package jadex.android.bluetooth.connection;

import jadex.android.bluetooth.device.IBluetoothAdapter.BluetoothState;
import jadex.android.bluetooth.device.IBluetoothDevice;
import jadex.android.bluetooth.device.IBluetoothDevice.BluetoothBondState;

public interface IBluetoothStateListener {
	void bluetoothStateChanged(BluetoothState newState, BluetoothState oldState);
	
	void bluetoothDeviceFound(IBluetoothDevice device);
	
	void bluetoothDeviceBondStateChanged(IBluetoothDevice device, BluetoothBondState newState, BluetoothBondState oldState);
}
