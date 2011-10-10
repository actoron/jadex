package jadex.android.bluetooth;

import jadex.android.bluetooth.device.IBluetoothDevice;


public interface ConnectionListener {
	void connectionStateChanged(IConnection connection);
	
	void messageReceived(DataPacket pkt, IBluetoothDevice fromDevice, IConnection incomingConnection);
}
