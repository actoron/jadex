package jadex.platform.service.awareness.discovery.registry;

import java.net.DatagramSocket;
import java.net.InetAddress;

import jadex.bridge.nonfunctional.annotation.NameValue;
import jadex.commons.SUtil;
import jadex.micro.annotation.AgentArgument;
import jadex.micro.annotation.Argument;
import jadex.micro.annotation.Arguments;
import jadex.micro.annotation.Description;
import jadex.micro.annotation.Properties;
import jadex.platform.service.awareness.discovery.ConnectionException;
import jadex.platform.service.awareness.discovery.MasterSlaveDiscoveryAgent;
import jadex.platform.service.awareness.discovery.ReceiveHandler;
import jadex.platform.service.awareness.discovery.SendHandler;

/**
 *  The registry awareness uses a dedicated registry awareness service at
 *  which all nodes register. Communication is done only via masters.
 *  Slaves send their infos to their master, which forwards them to the
 *  registry. Also the registry only sends infos to the masters that
 *  distribute them to their slaves. This avoids sending the infos to
 *  all slaves over the network.
 */
@Description("This agent looks for other awareness agents in the local net.")
@Arguments(replace=false, value=
{
//	@Argument(name="address", clazz=String.class, defaultvalue="\"192.168.56.1\"", description="The ip address of registry."),
	@Argument(name="address", clazz=String.class, defaultvalue="\"134.100.11.233\"", description="The ip address of registry."),
	@Argument(name="networkiface", clazz=String.class, description = "The network interface to listen on (for master instances)."),
	@Argument(name="port", clazz=int.class, defaultvalue="55699", description="The port used for finding other agents.")
})
@Properties(@NameValue(name="system", value="true"))
public class RegistryDiscoveryAgent extends MasterSlaveDiscoveryAgent
{
	//-------- attributes --------
	
	/** The registry internet address. */
	@AgentArgument(convert="java.net.InetAddress.getByName($value)")
	protected InetAddress address;

	/** The receiver port. */
	@AgentArgument
	protected int port;


//	/** The known platforms. */
//	protected LeaseTimeHandler knowns;
		
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
	
//	/**
//	 *  Execute the functional body of the agent.
//	 *  Is only called once.
//	 */
//	@AgentBody
//	public void executeBody()
//	{
//		this.knowns = new LeaseTimeHandler(this)
//		{
//			public void entryDeleted(DiscoveryEntry entry)
//			{
//				System.out.println("Entry deleted: "+entry.getInfo().getSender());
//				
//				InetSocketAddress addr = (InetSocketAddress)entry.getEntry();
//				if(isRegistry(addr.getAddress(), addr.getPort()))
//				{
//					System.out.println("Registry deleted");
//					initNetworkRessource();
//				}
//			}
//		};
//		
//		super.executeBody();
//	}
	
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
	
//	/**
//	 *  Get the knwon entries.
//	 */
//	public LeaseTimeHandler getKnowns()
//	{
//		return knowns;
//	}
	
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
				ret = isRegistry(SUtil.getInetAddress(networkiface), s.getLocalPort());
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
		return isMaster()? createMasterId(SUtil.getInetAddress(networkiface),
			getSocket().getLocalPort()): null;
	}
	
	/**
	 *  Get the local master id.
	 */
	protected String getMyMasterId()
	{
		return createMasterId(SUtil.getInetAddress(networkiface), port);
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
					// First one on dest ip becomes master.
					socket = new DatagramSocket(port);
					getMicroAgent().getLogger().info((address.equals(SUtil.getInetAddress(networkiface))?
						"registry: ": "master: ")+SUtil.getInetAddress(networkiface)+" "+port);
				}
				catch(Exception e)
				{
					// If not first it will be client and use any port.
				}
				
				if(socket==null)
				{
					try
					{
						socket = new DatagramSocket();
					}
					catch(Exception e)
					{
						getMicroAgent().getLogger().warning("Socket creation error: "+e);
						throw new ConnectionException(e);
//						e.printStackTrace();
					}
				}
			}
		}
		else if(socket==null)
		{
			throw new ConnectionException("No creation of socket in killed state.");
		}
		
		return socket;
	}
}


