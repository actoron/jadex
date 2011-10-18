package jadex.android.bluetooth.routing;

import jadex.android.bluetooth.exceptions.MessageNotSendException;
import jadex.android.bluetooth.message.DataPacket;

public interface IPacketSender {
	void sendMessageToConnectedDevice(DataPacket packet, String address) throws MessageNotSendException;
}
