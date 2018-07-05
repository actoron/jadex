package jadex.platform.service.awareness.discovery.ipscanner;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;

import jadex.bridge.ComponentTerminatedException;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.service.types.awareness.AwarenessInfo;
import jadex.commons.SUtil;
import jadex.commons.future.DefaultResultListener;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentArgument;
import jadex.micro.annotation.Argument;
import jadex.micro.annotation.Arguments;
import jadex.micro.annotation.Description;
import jadex.platform.service.awareness.discovery.ConnectionException;
import jadex.platform.service.awareness.discovery.DiscoveryAgent;
import jadex.platform.service.awareness.discovery.MasterSlaveDiscoveryAgent;
import jadex.platform.service.awareness.discovery.ReceiveHandler;
import jadex.platform.service.awareness.discovery.SendHandler;

/**
 *  Agent that sends multicasts to locate other Jadex awareness agents.
 */
@Description("This agent looks for other awareness agents in the local net.")
@Arguments(replace=false, value=
{
	@Argument(name="port", clazz=int.class, defaultvalue="55668", description="The port used for finding other agents."),
	@Argument(name="scanfactor", clazz=long.class, defaultvalue="1", description="The delay between scanning as factor of delay time, e.g. 1=10000, 2=20000."),
	@Argument(name="buffersize", clazz=int.class, defaultvalue="1024*1024", description="The size of the send buffer (determines the number of messages that can be sent at once).")
})
//@Properties(@NameValue(name="system", value="true"))
@Agent
public class ScannerDiscoveryAgent extends MasterSlaveDiscoveryAgent
{
	/** The receiver port. */
	@AgentArgument
	protected int port;
	
	/** The scan delay factor. */
	@AgentArgument
	protected int scanfactor;
	
	/** The buffer size. */
	@AgentArgument
	protected int buffersize;

	/** The socket to receive. */
	protected DatagramChannel channel;
	protected Selector selector;
	

	/**
	 *  Create the send handler.
	 */
	public SendHandler createSendHandler()
	{
		return new ScannerSendHandler(this);
	}
	
	/**
	 *  Create the receive handler.
	 */
	public ReceiveHandler createReceiveHandler()
	{
		return new ScannerReceiveHandler(this);
	}
	
	/**
	 *  Get the scanfactor.
	 *  @return the scanfactor.
	 */
	public int getScanFactor()
	{
		return scanfactor;
	}

	/**
	 *  Get the port.
	 *  @return the port.
	 */
	public int getPort()
	{
		return port;
	}

	/**
	 *  Test if is master.
	 */
	protected boolean isMaster()
	{
		return getChannel()!=null && this.port==getChannel().socket().getLocalPort();
	}
	
	/**
	 *  Create the master id.
	 */
	protected String createMasterId()
	{
		return isMaster()? createMasterId(SUtil.getInetAddress(),
			getChannel().socket().getLocalPort()): null;
	}
	
	/**
	 *  Get the local master id.
	 */
	protected String getMyMasterId()
	{
		return createMasterId(SUtil.getInetAddress(), port);
	}
	
	/**
	 *  Create the master id.
	 */
	protected String createMasterId(InetAddress address, int port)
	{
		return address+":"+port;
	}
	
	/**
	 *  (Re)init receiving.
	 */
	public synchronized void initNetworkRessource()
	{
		try
		{
			terminateNetworkRessource();
			getChannel();
		}
		catch(Exception e)
		{
		}
	}
	
	/**
	 *  Terminate receiving.
	 */
	protected synchronized void terminateNetworkRessource()
	{
		try
		{
			if(channel!=null)
			{
				channel.close();
				channel = null;
			}
		}
		catch(Exception e)
		{
		}
	}
	
	/**
	 *  Get or create a channel.
	 */
	protected synchronized DatagramChannel getChannel()
	{
		if(!isKilled())
		{
			if(channel==null)
			{
				try
				{
					channel = DatagramChannel.open();
					channel.configureBlocking(false);
					channel.socket().bind(new InetSocketAddress(port));
					channel.socket().setSendBufferSize(buffersize);
					// Register blocks when other thread waits in it.
					// Must be synchronized due to selector wakeup freeing other thread.
					synchronized(this)
					{
						if(selector==null)
							selector = Selector.open();
						selector.wakeup();
						channel.register(selector, SelectionKey.OP_READ);
					}
//					System.out.println("local master at: "+SUtil.getInet4Address()+" "+port);
				}
				catch(Exception e)
				{
					try
					{
						// In case the receiversocket cannot be opened
						// open another local socket at an arbitrary port
						// and send this port to the master.
						channel = DatagramChannel.open();
						channel.configureBlocking(false);
						channel.socket().bind(new InetSocketAddress(0));
						channel.socket().setSendBufferSize(buffersize);
						synchronized(this)
						{
							if(selector==null)
								selector = Selector.open();
							selector.wakeup();
							channel.register(selector, SelectionKey.OP_READ);
						}
						
						createAwarenessInfo(AwarenessInfo.STATE_ONLINE, createMasterId())
							.addResultListener(agent.getFeature(IExecutionFeature.class).createResultListener(new DefaultResultListener<AwarenessInfo>(agent.getLogger())
						{
							public void resultAvailable(AwarenessInfo info)
							{
								InetAddress address = SUtil.getInetAddress();
//								byte[] data = DiscoveryState.encodeObject(info, getMicroAgent().getModel().getClassLoader());
								byte[] data = DiscoveryAgent.encodeObject(info, getMicroAgent().getClassLoader());
								((ScannerSendHandler)sender).send(data, address, port);
							}
							
							public void exceptionOccurred(Exception exception)
							{
								if(!(exception instanceof ComponentTerminatedException))
									super.exceptionOccurred(exception);
							}
						}));
						
//						AwarenessInfo info = createAwarenessInfo(AwarenessInfo.STATE_OFFLINE, createMasterId());
////						byte[] data = DiscoveryState.encodeObject(info, getMicroAgent().getModel().getClassLoader());
//						byte[] data = DiscoveryAgent.encodeObject(info, getMicroAgent().getClassLoader());
//						((ScannerSendHandler)sender).send(data, address, port);
						
//						System.out.println("local slave at: "+SUtil.getInet4Address()+" "+channel.socket().getLocalPort());
//						getLogger().warning("Running in local mode: "+e);
					}
					catch(Exception e2)
					{
//						e2.printStackTrace();
						getMicroAgent().getLogger().warning("Channel problem: "+e2);
						throw new ConnectionException(e2);
					}
				}
			}
		}
		
		return channel;
	}
}
