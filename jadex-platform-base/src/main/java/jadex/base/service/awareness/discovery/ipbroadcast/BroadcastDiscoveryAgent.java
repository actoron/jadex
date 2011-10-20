package jadex.base.service.awareness.discovery.ipbroadcast;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;

import jadex.base.service.awareness.discovery.DiscoveryService;
import jadex.base.service.awareness.discovery.DiscoveryState;
import jadex.base.service.awareness.discovery.MasterSlaveDiscoveryAgent;
import jadex.base.service.awareness.discovery.ReceiveHandler;
import jadex.base.service.awareness.discovery.SendHandler;
import jadex.base.service.awareness.discovery.ipmulticast.MulticastReceiveHandler;
import jadex.base.service.awareness.discovery.ipmulticast.MulticastSendHandler;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.types.awareness.AwarenessInfo;
import jadex.bridge.service.types.awareness.IDiscoveryService;
import jadex.bridge.service.types.awareness.IManagementService;
import jadex.bridge.service.types.threadpool.IThreadPoolService;
import jadex.commons.SUtil;
import jadex.micro.annotation.AgentArgument;
import jadex.micro.annotation.Argument;
import jadex.micro.annotation.Arguments;
import jadex.micro.annotation.Binding;
import jadex.micro.annotation.Configuration;
import jadex.micro.annotation.Configurations;
import jadex.micro.annotation.Description;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.NameValue;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;

/**
 *  Agent that sends multicasts to locate other Jadex awareness agents.
 */
@Description("This agent looks for other awareness agents in the local net.")
@Arguments(
{
	@Argument(name="port", clazz=int.class, defaultvalue="55670", description="The port used for finding other agents."),
	@Argument(name="delay", clazz=long.class, defaultvalue="10000", description="The delay between sending awareness infos (in milliseconds).")
//	@Argument(name="fast", clazz=boolean.class, defaultvalue="true", description="Flag for enabling fast startup awareness (pingpong send behavior)."),
})
@Configurations(
{
	@Configuration(name="Frequent updates (10s)", arguments=@NameValue(name="delay", value="10000")),
	@Configuration(name="Medium updates (20s)", arguments=@NameValue(name="delay", value="20000")),
	@Configuration(name="Seldom updates (60s)", arguments=@NameValue(name="delay", value="60000"))
})
@ProvidedServices(
	@ProvidedService(type=IDiscoveryService.class, implementation=@Implementation(DiscoveryService.class))
)
@RequiredServices(
{
	@RequiredService(name="threadpool", type=IThreadPoolService.class, binding=@Binding(scope=RequiredServiceInfo.SCOPE_PLATFORM)),
	@RequiredService(name="management", type=IManagementService.class, binding=@Binding(scope=RequiredServiceInfo.SCOPE_PLATFORM))
})
public class BroadcastDiscoveryAgent extends MasterSlaveDiscoveryAgent
{
	/** The receiver port. */
	@AgentArgument
	protected int port;
		
	/** The socket. */
	protected DatagramSocket socket;
	
	/** The receive buffer. */
	protected byte[] buffer;

	//-------- methods --------
	
	/**
	 *  Create the send handler.
	 */
	public SendHandler createSendHandler()
	{
		return new BroadcastSendHandler(this);
	}
	
	/**
	 *  Create the receive handler.
	 */
	public ReceiveHandler createReceiveHandler()
	{
		return new BroadcastReceiveHandler(this);
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
		return getSocket()!=null && this.port==getSocket().getLocalPort();
	}
	
	/**
	 *  Create the master id.
	 */
	protected String createMasterId()
	{
		return isMaster()? createMasterId(SUtil.getInet4Address(),
			getSocket().getLocalPort()): null;
	}
	
	/**
	 *  Get the local master id.
	 */
	protected String getMyMasterId()
	{
		return createMasterId(SUtil.getInet4Address(), port);
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
			getSocket();
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
			if(socket!=null)
			{
				socket.close();
				socket = null;
			}
		}
		catch(Exception e)
		{
		}
	}
	
	/**
	 *  Get or create a receiver socket.
	 *  
	 *  Note, this method has to be synchronized.
	 *  Is called from receiver as well as component thread.
	 */
	protected synchronized DatagramSocket getSocket()
	{
		if(!isKilled())
		{
			if(socket==null)
			{
				try
				{
					socket = new DatagramSocket(port);
					socket.setBroadcast(true);
//					System.out.println("local master at: "+SUtil.getInet4Address()+" "+port);
					getMicroAgent().getLogger().info("local master at: "+SUtil.getInet4Address()+" "+port);
				}
				catch(Exception e)
				{
					try
					{
						// In case the receiversocket cannot be opened
						// open another local socket at an arbitrary port
						// and send this port to the master.
						socket = new DatagramSocket();
						socket.setBroadcast(true);
						InetAddress address = SUtil.getInet4Address();
						AwarenessInfo info = createAwarenessInfo(AwarenessInfo.STATE_ONLINE, createMasterId());
//						byte[] data = DiscoveryState.encodeObject(info, getMicroAgent().getModel().getClassLoader());
						byte[] data = DiscoveryState.encodeObject(info, getMicroAgent().getClassLoader());
						((BroadcastSendHandler)sender).send(data, address, port);
//						System.out.println("local slave at: "+SUtil.getInet4Address()+" "+socket.getLocalPort());
						getMicroAgent().getLogger().info("local slave at: "+SUtil.getInet4Address()+" "+socket.getLocalPort());
					}
					catch(Exception e2)
					{
//						e2.printStackTrace();
						throw new RuntimeException(e2);
					}
				}
			}
		}

		return socket;
	}
}
