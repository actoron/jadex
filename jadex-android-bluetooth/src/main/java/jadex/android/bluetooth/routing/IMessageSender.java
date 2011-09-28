package jadex.android.bluetooth.routing;

import jadex.android.bluetooth.DataPacket;
import jadex.android.bluetooth.IConnection;
import android.bluetooth.BluetoothDevice;

public interface IMessageSender {
	void sendMessageToConnectedDevice(DataPacket packet, String address);
}
