package jadex.platform.service.pawareness;

import java.net.DatagramSocket;
import java.net.InetSocketAddress;

import jadex.bridge.service.annotation.ServiceStart;
import jadex.commons.Boolean3;
import jadex.micro.annotation.Agent;

/**
 *  Implements passive awareness via broadcast.
 */
//@Service
@Agent(autoprovide = Boolean3.TRUE,
	predecessors="jadex.platform.service.address.TransportAddressAgent",
	successors="jadex.platform.service.registryv2.SuperpeerClientAgent",
	autostart=Boolean3.FALSE
)
public class PassiveAwarenessBroadcastAgent	extends PassiveAwarenessLocalNetworkBaseAgent
{
	/**
	 *  At startup create a multicast socket for listening.
	 */
	@ServiceStart
	public void	start() throws Exception
	{
		address = "255.255.255.255";
		port = 33091;
		
		sendsocket = new DatagramSocket(0);
		sendsocket.setBroadcast(true);
		sendsocket.setReuseAddress(true);
		recvsocket = new DatagramSocket(null);
		recvsocket.setReuseAddress(true);
		recvsocket.bind(new InetSocketAddress(port));
		
		super.start();
	}
}
