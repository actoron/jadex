package jadex.platform.service.pawareness;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.annotation.ServiceShutdown;
import jadex.bridge.service.annotation.ServiceStart;
import jadex.commons.Boolean3;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.Argument;
import jadex.micro.annotation.Arguments;

/**
 *  Implements passive awareness via multicast.
 */
@Service
@Agent(autoprovide = Boolean3.TRUE, autostart=Boolean3.TRUE,
	predecessors="jadex.platform.service.address.TransportAddressAgent",
	successors="jadex.platform.service.registryv2.SuperpeerClientAgent"
)
@Arguments({
	@Argument(name="address", clazz=String.class, defaultvalue="\"232.0.9.1\""),
	@Argument(name="port", clazz=int.class, defaultvalue="32091")
})
public class PassiveAwarenessMulticastAgent	extends PassiveAwarenessLocalNetworkBaseAgent
{
	/**
	 *  At startup create a multicast socket for listening.
	 */
	@ServiceStart
	public void	start() throws Exception
	{
		sendsocket	= new DatagramSocket(0);
		recvsocket = new MulticastSocket(port);
		((MulticastSocket)recvsocket).joinGroup(InetAddress.getByName(address));
		
		super.start();
	}

	/**
	 * Stop the service.
	 */
	@ServiceShutdown
	public void shutdown() throws Exception
	{
		((MulticastSocket)recvsocket).leaveGroup(InetAddress.getByName(address));
		
		super.shutdown();
	}
}
