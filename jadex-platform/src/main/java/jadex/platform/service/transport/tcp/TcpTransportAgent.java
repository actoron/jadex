package jadex.platform.service.transport.tcp;

import java.nio.channels.SocketChannel;

import jadex.platform.service.transport.AbstractRawTransportAgent;
import jadex.platform.service.transport.ITransport;

public class TcpTransportAgent extends AbstractRawTransportAgent<SocketChannel>
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
