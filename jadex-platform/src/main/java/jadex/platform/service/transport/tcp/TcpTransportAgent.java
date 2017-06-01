package jadex.platform.service.transport.tcp;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.nio.channels.SocketChannel;

import jadex.commons.SUtil;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.micro.annotation.AgentArgument;
import jadex.micro.annotation.AgentKilled;
import jadex.platform.service.transport.AbstractTransportAgent;

public class TcpTransportAgent extends AbstractTransportAgent<SocketChannel>
{
	//-------- arguments --------
	
	/** The port, the transport should listen to (&lt;0: don't listen, 0: choose random port, >0: use given port). */
	@AgentArgument
	protected int	port	= 0;
	
	//-------- attributes --------
	
	/** The selector thread for asynchronous I/O operations, if any. */
	protected TcpSelectorThread	selectorthread;
	
	//-------- life cycle --------
	
	/**
	 *  Start selector thread.
	 */
	@Override
	protected void init() throws Exception
	{
		super.init();
		
		// Set up server, if port given.
		// If port==0 -> any free port
		if(port>=0)
		{
			this.selectorthread	= new TcpSelectorThread(agent);
			int	port	= selectorthread.openPort(this.port); 
			
			// Announce connection addresses.
			InetAddress[]	addresses	= SUtil.getNetworkAddresses();
			String[]	saddresses	= new String[addresses.length];
			for(int i=0; i<addresses.length; i++)
			{
				if(addresses[i] instanceof Inet6Address)
				{
					saddresses[i]	= "[" + addresses[i].getHostAddress() + "]:" + port;
				}
				else // if (address instanceof Inet4Address)
				{
					saddresses[i]	= addresses[i].getHostAddress() + ":" + port;
				}
			}
			announceAddresses(saddresses);
			
			selectorthread.start();
		}
	}
	
	@AgentKilled
	protected void	shutdown()
	{
		if(selectorthread!=null)
		{
			selectorthread.stop();
			selectorthread	= null;
		}
	}
	
	//-------- abstract methods to be provided by concrete transport --------
	
	/**
	 *  Get the protocol name.
	 */
	public String	getProtocolName()
	{
		return "tcp";
	}
	
	/**
	 *  Create a connection to a given address.
	 */
	protected IFuture<SocketChannel>	createConnection(String address)
	{
		try
		{
			if(selectorthread==null)
			{
				this.selectorthread	= new TcpSelectorThread(agent);			
				selectorthread.start();
			}
			return selectorthread.createConnection(address);
		}
		catch(Exception e)
		{
			return new Future<SocketChannel>(e);
		}
	}
	
	/**
	 *  Close a previously opened connection.
	 */
	protected void	closeConnection(SocketChannel con)
	{
		
	}
	
	/**
	 *  Send a message over a given transport.
	 */
	protected IFuture<Void>	doSendMessage(SocketChannel con, byte[] header, byte[] body)
	{
		assert selectorthread!=null:"Thread should have been created during connection establishment.";
		return selectorthread.sendMessage(con, header, body);
	}
}
