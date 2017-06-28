package jadex.platform.service.transport.tcp;

import java.nio.channels.SocketChannel;

import jadex.platform.service.transport.AbstractTransportAgent;
import jadex.platform.service.transport.ITransport;

public class TcpTransportAgent extends AbstractTransportAgent<SocketChannel>
{
	//-------- abstract methods to be provided by concrete transport --------
	
	/**
	 *  Get the transport implementation
	 */
	public ITransport<SocketChannel>	createTransportImpl()
	{
		return new TcpTransport();
	}
}
