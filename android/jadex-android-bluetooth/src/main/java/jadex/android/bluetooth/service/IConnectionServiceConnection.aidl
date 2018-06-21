package jadex.android.bluetooth.service;

import jadex.android.bluetooth.service.IBTP2PMessageCallback;
import jadex.android.bluetooth.service.IBTP2PAwarenessInfoCallback;
import jadex.android.bluetooth.message.BluetoothMessage;
import jadex.android.bluetooth.device.IBluetoothDevice;

// Declare the interface.

interface IConnectionServiceConnection {
	void scanEnvironment();
	
	void startBTServer();
	
	void stopBTServer();
	
	void startAutoConnect();
	
	void stopAutoConnect();
	
	void registerMessageCallback(IBTP2PMessageCallback callback);
	
	void registerAwarenessInfoCallback(IBTP2PAwarenessInfoCallback callback);
	
	IBluetoothDevice[] getUnbondedDevicesInRange();
	
	IBluetoothDevice[] getBondedDevicesInRange();
	
	IBluetoothDevice[] getConnectedDevices();
	
	IBluetoothDevice[] getReachableDevices();
	
	String getBTAddress();
	
	void connectToDevice(in IBluetoothDevice dev);
	
	void disconnectDevice(in IBluetoothDevice dev);
	
	void sendMessage(in BluetoothMessage msg);
}
