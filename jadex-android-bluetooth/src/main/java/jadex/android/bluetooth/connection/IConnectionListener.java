package jadex.android.bluetooth.connection;

import jadex.android.bluetooth.device.IBluetoothDevice;
import jadex.android.bluetooth.message.DataPacket;


public interface IConnectionListener {
	void connectionStateChanged(IConnection connection);
	
	void messageReceived(DataPacket pkt, IBluetoothDevice fromDevice, IConnection incomingConnection);
	
	void messageNotSent(DataPacket pkt);
}
