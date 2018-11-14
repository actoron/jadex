package jadex.android.bluetooth.routing;

import jadex.android.bluetooth.exceptions.MessageNotSendException;
import jadex.android.bluetooth.message.DataPacket;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Abstract Packet Router Class, provides basic Listener functionality.
 * @author Julian Kalinowski
 *
 */
public abstract class AbstractPacketRouter implements IPacketRouter {

	private IPacketSender sender;
	protected String ownAddress;
	private Set<RoutingEntriesChangeListener> listeners;

	/**
	 * Constructor
	 */
	public AbstractPacketRouter() {
		listeners = new CopyOnWriteArraySet<IPacketRouter.RoutingEntriesChangeListener>();
	}

	@Override
	public void addRoutingEntriesChangedListener(RoutingEntriesChangeListener l) {
		listeners.add(l);
	}

	@Override
	public boolean removeReachableDevicesChangeListener(RoutingEntriesChangeListener l) {
		return listeners.remove(l);
	}

	@Override
	public String getOwnAddress() {
		return ownAddress;
	}

	@Override
	public void setOwnAddress(String address) {
		ownAddress = address;
	}

	@Override
	public void setPacketSender(IPacketSender sender) {
		this.sender = sender;
	}

	protected void notifyReachableDevicesChanged() {
		for (RoutingEntriesChangeListener l : listeners) {
			l.reachableDevicesChanged();
		}
	}

	protected void notifyConnectedDevicesChanged() {
		for (RoutingEntriesChangeListener l : listeners) {
			l.connectedDevicesChanged();
		}
	}
	
	/**
	 * Sends the message using the PacketSender
	 * @param pkt Paket to send
	 * @param address Bluetooth Address as String
	 * @throws MessageNotSendException if message could not be sent
	 */
	protected void sendMessageToConnectedDevice(DataPacket pkt, String address) throws MessageNotSendException {
		sender.sendMessageToConnectedDevice(pkt, address);
	}
}
