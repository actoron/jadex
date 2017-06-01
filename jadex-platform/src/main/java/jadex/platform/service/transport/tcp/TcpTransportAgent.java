package jadex.platform.service.transport.tcp;

import java.io.IOException;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.threadpool.IDaemonThreadPoolService;
import jadex.commons.SUtil;
import jadex.commons.future.IFuture;
import jadex.micro.annotation.AgentArgument;
import jadex.platform.service.transport.AbstractTransportAgent;

public class TcpTransportAgent extends AbstractTransportAgent<SocketChannel>
{
	//-------- arguments --------
	
	/** The port, the transport should listen to (&lt;0: don't listen, 0: choose random port, >0: use given port). */
	@AgentArgument
	protected int	port	= 0;
	
	//-------- attributes --------
	
	/** The selector thread for asynchronous I/O operations. */
	protected TcpSelectorThread	selectorthread;
	
	//-------- life cycle --------
	
	/**
	 *  Open server socket, if any.
	 */
	@Override
	protected void init()
	{
		super.init();

		ServerSocketChannel	ssc	= null;
		try
		{
			// ANDROID: Selector.open() causes an exception in a 2.2
			// emulator due to IPv6 addresses, see:
			// http://code.google.com/p/android/issues/detail?id=9431
			// Causes problem with maven too (only with Win firewall?)
			// http://www.thatsjava.com/java-core-apis/28232/
			java.lang.System.setProperty("java.net.preferIPv4Stack", "true");
			java.lang.System.setProperty("java.net.preferIPv6Addresses", "false");
			final Selector selector = Selector.open();
			
			// Set up receiver side, if any.
			// Better be done before selector thread is started due to deadlocks: https://stackoverflow.com/questions/12822298/nio-selector-how-to-properly-register-new-channel-while-selecting 
			// If port==0 -> any free port
			if(port>=0)
			{	
				ssc = ServerSocketChannel.open();
				ssc.configureBlocking(false);
				ServerSocket serversocket = ssc.socket();
				serversocket.bind(new InetSocketAddress(port));
				ssc.register(selector, SelectionKey.OP_ACCEPT);
				
				// Announce connection addresses.
				int	port = serversocket.getLocalPort();
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
				agent.getLogger().info("TCP transport listening to port: "+port);
			}

			// Start selector thread for asynchronous sending and/or receiving
			IDaemonThreadPoolService	tps	= SServiceProvider.getLocalService(agent, IDaemonThreadPoolService.class, RequiredServiceInfo.SCOPE_PLATFORM);
			this.selectorthread	= new TcpSelectorThread(selector, agent);
			tps.execute(selectorthread);
		}
		catch(Exception e)
		{
			if(ssc!=null)
			{
				try
				{
					ssc.close();
				}catch(IOException e2){}
			}
			throw new RuntimeException("Transport initialization error", e);
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
		return selectorthread.createConnection(address);
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
		return selectorthread.sendMessage(con, header, body);
	}
}
