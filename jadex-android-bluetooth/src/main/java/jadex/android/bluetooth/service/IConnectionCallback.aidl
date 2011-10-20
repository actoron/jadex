package jadex.android.bluetooth.service;

import jadex.android.bluetooth.device.IBluetoothDevice;

// Declare the interface.
oneway interface IConnectionCallback {
//  void incomingConnection(String device);
//  void maxConnectionsReached();
//  void messageReceived(String device, in byte[] message);
//  void connectionLost(String device);
	void deviceListChanged();
	
	void knownDevicesChanged(in IBluetoothDevice[] knownDevices);
	
	void messageReceived(in byte[] data);
	
	void awarenessInfoReceived(in byte[] data);
}
