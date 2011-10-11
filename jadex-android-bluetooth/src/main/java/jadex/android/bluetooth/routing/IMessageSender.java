package jadex.android.bluetooth.routing;

import jadex.android.bluetooth.message.DataPacket;

public interface IMessageSender {
	void sendMessageToConnectedDevice(DataPacket packet, String address);
}
