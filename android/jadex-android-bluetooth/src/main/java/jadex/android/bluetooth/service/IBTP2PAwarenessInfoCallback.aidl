package jadex.android.bluetooth.service;

import jadex.android.bluetooth.device.IBluetoothDevice;

// Declare the interface.
oneway interface IBTP2PAwarenessInfoCallback {
	void knownDevicesChanged(in IBluetoothDevice[] knownDevices);
	
	void awarenessInfoReceived(in byte[] data);
}
