package jadex.platform.service.transport.tcp;

import java.nio.channels.SocketChannel;

import jadex.commons.Boolean3;
import jadex.micro.annotation.Agent;
import jadex.platform.service.transport.AbstractTransportAgent;
import jadex.platform.service.transport.ITransport;

/**
 *  Agent that implements TCP message transport.
 */
@Agent(name="tcp", autostart=Boolean3.TRUE)
public class TcpTransportAgent extends AbstractTransportAgent<SocketChannel>
{
	//-------- abstract methods to be provided by concrete transport --------
	
	/**
	 *  Get the transport implementation
	 */
	public ITransport<SocketChannel>	createTransportImpl()
	{
		return new TcpTransport(maxmsgsize);
	}
}
