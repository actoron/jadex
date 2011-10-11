package jadex.android.bluetooth.service;

import jadex.android.bluetooth.service.IConnectionCallback;
import jadex.android.bluetooth.message.BluetoothMessage;
import jadex.android.bluetooth.device.IBluetoothDevice;

// Declare the interface.

interface IConnectionServiceConnection {
	void scanEnvironment();
	
	void startBTServer();
	
	void stopBTServer();
	
	void startAutoConnect();
	
	void stopAutoConnect();
	
	void registerCallback(IConnectionCallback callback);
	
	IBluetoothDevice[] getUnbondedDevicesInRange();
	
	IBluetoothDevice[] getBondedDevicesInRange();
	
	IBluetoothDevice[] getConnectedDevices();
	
	IBluetoothDevice[] getReachableDevices();
	
	void connectToDevice(in IBluetoothDevice dev);
	
	void disconnectDevice(in IBluetoothDevice dev);
	
	void sendMessage(in BluetoothMessage msg);
}
