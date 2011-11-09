package jadex.base.service.awareness.discovery.ipmulticast;

import jadex.base.service.awareness.discovery.DiscoveryAgent;
import jadex.base.service.awareness.discovery.DiscoveryService;
import jadex.base.service.awareness.discovery.ReceiveHandler;
import jadex.base.service.awareness.discovery.SendHandler;
import jadex.bridge.service.RequiredServiceInfo;
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

import java.net.InetAddress;
import java.net.MulticastSocket;

/**
 *  Agent that sends multicasts to locate other Jadex awareness agents.
 */
@Description("This agent looks for other awareness agents in the local net.")
@Arguments(
{
	@Argument(name="address", clazz=String.class, defaultvalue="\"224.0.0.0\"", description="The ip multicast address used for finding other agents (range 224.0.0.0-239.255.255.255)."),
	@Argument(name="port", clazz=int.class, defaultvalue="55667", description="The port used for finding other agents."),
	@Argument(name="delay", clazz=long.class, defaultvalue="10000", description="The delay between sending awareness infos (in milliseconds)."),
	@Argument(name="fast", clazz=boolean.class, defaultvalue="true", description="Flag for enabling fast startup awareness (pingpong send behavior).")
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
public class MulticastDiscoveryAgent extends DiscoveryAgent
{
	//-------- attributes --------
	
	/** The multicast internet address. */
	@AgentArgument(convert="java.net.InetAddress.getByName($value)")
	protected InetAddress address;
	
	/** The receiver port. */
	@AgentArgument
	protected int port;
	
	/** The socket to send/receive. */
	protected MulticastSocket socket;

	/** The current receive address. */
	protected InetAddress myaddress;
	
	//-------- methods --------
	
	/**
	 *  Create the send handler.
	 */
	public SendHandler createSendHandler()
	{
		return new MulticastSendHandler(this);
	}
	
	/**
	 *  Create the receive handler.
	 */
	public ReceiveHandler createReceiveHandler()
	{
		return new MulticastReceiveHandler(this);
	}
	
	/**
	 *  Get the address.
	 *  @return the address.
	 */
	public Object[] getAddressInfo()
	{
		return new Object[]{address, new Integer(port)};
	}

	/**
	 *  Set the address.
	 *  @param address The address to set.
	 */
	public void setAddressInfo(InetAddress address, int port)
	{
//		System.out.println("setAddress: "+address+" "+port);
		this.address = address;
		this.port = port;
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
	 *  Get or create a socket.
	 */
	protected synchronized MulticastSocket getSocket()
	{
		if(!isKilled())
		{
			Object[] ai = getAddressInfo();
			InetAddress curaddress = (InetAddress)ai[0];
			int curport = ((Integer)ai[1]).intValue();
			
			if(socket!=null && (socket.getLocalPort()!=curport || !SUtil.equals(curaddress, myaddress)))
			{
				try
				{
					socket.leaveGroup(myaddress);
					socket.close();
				}
				catch(Exception e)
				{
				}
				socket = null;
			}
			if(socket==null)
			{
				try
				{
					socket = new MulticastSocket(curport);
					// Does not receive messages on same host if disabled.
//					socket.setLoopbackMode(true);
					socket.joinGroup(curaddress);
					myaddress = curaddress;
				}
				catch(Exception e)
				{
					socket	= null;
					getMicroAgent().getLogger().warning("Awareness error when joining multicast group: "+e);
					throw new RuntimeException(e);
				}
			}
		}
		
		return socket;
	}
}
