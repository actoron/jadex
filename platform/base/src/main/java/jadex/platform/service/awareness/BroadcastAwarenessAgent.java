package jadex.platform.service.awareness;

import java.net.DatagramSocket;
import java.net.InetSocketAddress;

import jadex.commons.Boolean3;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.Argument;
import jadex.micro.annotation.Arguments;
import jadex.micro.annotation.OnInit;

/**
 *  Implements passive awareness via broadcast.
 */
//@Service
@Agent(autoprovide = Boolean3.TRUE, autostart=Boolean3.TRUE)
@Arguments({
	@Argument(name="address", clazz=String.class, defaultvalue="\"255.255.255.255\""),
	@Argument(name="port", clazz=int.class, defaultvalue="33091")
})
public class BroadcastAwarenessAgent extends LocalNetworkAwarenessBaseAgent
{
	/**
	 *  At startup create a multicast socket for listening.
	 */
	//@AgentCreated
	@OnInit
	public void	init() throws Exception
	{
		sendsocket = new DatagramSocket(0);
		sendsocket.setBroadcast(true);
		sendsocket.setReuseAddress(true);
		recvsocket = new DatagramSocket(null);
		recvsocket.setReuseAddress(true);
		recvsocket.bind(new InetSocketAddress(port));
		
		super.init();
	}
}
