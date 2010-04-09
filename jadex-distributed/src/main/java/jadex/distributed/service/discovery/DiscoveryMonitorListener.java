package jadex.distributed.service.discovery;

import java.net.InetAddress;

public interface DiscoveryMonitorListener {

	/**
	 * Called when a new slave sent a HELLO message to announce his presence.
	 * @param addr - IP of the just appeared slave
	 */
	public void handleSlaveHello(InetAddress addr);
	
	/**
	 * Called when a slave sent a BYE message to announce his disappearance.
	 * @param addr - IP of the disappearing slave
	 */
	public void handleSlaveBye(InetAddress addr);
}
