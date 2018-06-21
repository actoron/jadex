package jadex.android.bluetooth.service;

import jadex.android.bluetooth.device.IBluetoothDevice;

// Declare the interface.
oneway interface IBTP2PMessageCallback {
//  void incomingConnection(String device);
//  void maxConnectionsReached();
//  void messageReceived(String device, in byte[] message);
//  void connectionLost(String device);
//	void deviceListChanged();
	
//	void knownDevicesChanged(in IBluetoothDevice[] knownDevices);
	
	void messageReceived(in String remoteAddress, in byte[] data);
}
