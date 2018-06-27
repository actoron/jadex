package jadex.android.bluetooth.routing;

import jadex.android.bluetooth.exceptions.MessageNotSendException;
import jadex.android.bluetooth.message.DataPacket;

/**
 * Interface for Classes, that are capable of sending Messages to directly connected Devices
 * @author Julian Kalinowski
 *
 */
public interface IPacketSender {
	/**
	 * Sends a DataPacket to the directly connected Device with the specified address
	 * @param packet {@link DataPacket} to send
	 * @param address Target Address
	 * @throws MessageNotSendException if Packet could not be sent
	 */
	void sendMessageToConnectedDevice(DataPacket packet, String address) throws MessageNotSendException;
}
