package jadex.android.bluetooth;

import jadex.android.bluetooth.device.IBluetoothDevice;
import android.bluetooth.BluetoothDevice;


public interface ConnectionListener {
	void connectionStateChanged(IConnection connection);
	
	void messageReceived(DataPacket pkt, IBluetoothDevice fromDevice, IConnection incomingConnection);
}
