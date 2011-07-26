package jadex.base.service.awareness.discovery.registry;

import jadex.base.service.awareness.AwarenessInfo;
import jadex.base.service.awareness.discovery.DiscoveryAgent;
import jadex.base.service.awareness.discovery.DiscoveryEntry;
import jadex.base.service.awareness.discovery.DiscoveryService;
import jadex.base.service.awareness.discovery.IDiscoveryService;
import jadex.base.service.awareness.discovery.LeaseTimeHandler;
import jadex.base.service.awareness.discovery.ReceiveHandler;
import jadex.base.service.awareness.discovery.SendHandler;
import jadex.base.service.awareness.management.IManagementService;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.threadpool.IThreadPoolService;
import jadex.commons.SUtil;
import jadex.micro.annotation.AgentArgument;
import jadex.micro.annotation.AgentBody;
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

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;

/**
 *  Agent that sends multicasts to locate other Jadex awareness agents.
 */
@Description("This agent looks for other awareness agents in the local net.")
@Arguments(
{
//	@Argument(name="address", clazz=String.class, defaultvalue="\"192.168.56.1\"", description="The ip address of registry."),
	@Argument(name="address", clazz=String.class, defaultvalue="\"134.100.11.233\"", description="The ip address of registry."),
	@Argument(name="port", clazz=int.class, defaultvalue="55699", description="The port used for finding other agents."),
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
public class RegistryDiscoveryAgent extends DiscoveryAgent
{
	//-------- attributes --------
	
	/** The registry internet address. */
	@AgentArgument(convert="java.net.InetAddress.getByName($value)")
	protected InetAddress address;

	/** The receiver port. */
	@AgentArgument
	protected int port;
	
	/** The known platforms. */
	protected LeaseTimeHandler knowns;
		
	/** The socket to send/receive. */
	protected DatagramSocket socket;		
	
	//-------- methods --------
	
	/**
	 *  Create the send handler.
	 */
	public SendHandler createSendHandler()
	{
		return new RegistrySendHandler(this);
	}
	
	/**
	 *  Create the receive handler.
	 */
	public ReceiveHandler createReceiveHandler()
	{
		return new RegistryReceiveHandler(this);
	}
	
	/**
	 *  Execute the functional body of the agent.
	 *  Is only called once.
	 */
	@AgentBody
	public void executeBody()
	{
		this.knowns = new LeaseTimeHandler(this)
		{
			public void entryDeleted(DiscoveryEntry entry)
			{
				System.out.println("Entry deleted: "+entry.getInfo().getSender());
				
				InetSocketAddress addr = (InetSocketAddress)entry.getEntry();
				if(isRegistry(addr.getAddress(), addr.getPort()))
				{
					System.out.println("Master deleted");
					initNetworkRessource();
				}
			}
		};
		
		super.executeBody();
	}
	
	/**
	 *  Get the address.
	 *  @return the address.
	 */
	public InetAddress getAddress()
	{
		return address;
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
	 *  Get the knwon entries.
	 */
	public LeaseTimeHandler getKnowns()
	{
		return knowns;
	}
	
	/**
	 *  Get the registry.
	 *  @return the registry.
	 */
	public boolean isRegistry()
	{
		boolean ret = false;
		try
		{
			DatagramSocket s = getSocket();
			if(s!=null)
			{
//				System.out.println("a: "+s.getLocalPort()+" "+port+" "+address+" "+SUtil.getInet4Address());
				ret = isRegistry(SUtil.getInet4Address(), s.getLocalPort());
			}
		}
		catch(Exception e) 
		{
		}
		return ret;
	}
	
	/**
	 *  Get the registry.
	 *  @return the registry.
	 */
	public boolean isRegistry(InetAddress address, int port)
	{
		return address.equals(this.address) && port==this.port;
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
				// Try to become registry
				if(address.equals(SUtil.getInet4Address()))
				{
					try
					{
						// First one on dest ip becomes registry.
						socket = new DatagramSocket(port);
						System.out.println("registry: "+SUtil.getInet4Address()+" "+port);
					}
					catch(Exception e)
					{
						// If not first it will be client and use any port.
					}
				}
				
				if(socket==null)
				{
					try
					{
						socket = new DatagramSocket();
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}
				}
			}
		}
		else if(socket==null)
		{
			throw new RuntimeException("No creation of socket in killed state.");
		}
		
		return socket;
	}
}


