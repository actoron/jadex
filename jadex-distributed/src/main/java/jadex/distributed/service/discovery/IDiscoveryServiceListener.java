package jadex.distributed.service.discovery;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Set;

/**
 * Interface which has to implemented by a object, which wants to be notified
 * by the DummyDiscoveryService when new machines are available.
 * 
 * @author daniel
 */
public interface IDiscoveryServiceListener {
	
	/**
	 * Notify a registered listener of type IDiscoveryListener that the list of known
	 * machines changed, so the listener should get a new snapshot to read it.
	 */
	public void notifyIDiscoveryListener();
	
	/**
	 * Notify a registered listener of type IDiscoveryListner that a new slave
	 * platform is available. The InetAddress of the new available slave is
	 * supplied with the <code>parameter</code>.
	 * @param addr
	 */
	public void notifyIDiscoveryListenerAdd(InetAddress addr);
	
	/**
	 * Notify a registered listener of type IDiscoveryListner that a old slave
	 * platform is not available anymore; the slave platform leaved the group of
	 * platforms. The InetAddress of the disappearing slave is
	 * supplied with the <code>parameter</code>.
	 * @param addr
	 */
	public void notifyIDiscoveryListenerRemove(InetAddress addr);
}
