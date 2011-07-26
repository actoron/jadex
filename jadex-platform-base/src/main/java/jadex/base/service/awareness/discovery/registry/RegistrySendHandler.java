package jadex.base.service.awareness.discovery.registry;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;

import jadex.base.service.awareness.AwarenessInfo;
import jadex.base.service.awareness.discovery.DiscoveryAgent;
import jadex.base.service.awareness.discovery.DiscoveryEntry;
import jadex.base.service.awareness.discovery.DiscoveryState;
import jadex.base.service.awareness.discovery.SendHandler;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.commons.SUtil;
import jadex.xml.annotation.XMLClassname;

/**
 *  Handle sending.
 */
class RegistrySendHandler extends SendHandler
{	
	/**
	 *  Create a new lease time handling object.
	 */
	public RegistrySendHandler(DiscoveryAgent agent)
	{
		super(agent);
	}
	
	/**
	 *  Start sending awareness infos.
	 *  (Ends automatically when a new send behaviour is started).
	 */
	public void startSendBehavior()
	{
		if(getAgent().isStarted())
		{
			final String sendid = SUtil.createUniqueId(getAgent().getMicroAgent()
				.getComponentIdentifier().getLocalName());
			this.sendid = sendid;	
			
			getAgent().getMicroAgent().scheduleStep(new IComponentStep()
			{
				@XMLClassname("send")
				public Object execute(IInternalAccess ia)
				{
					if(!getAgent().isKilled() && sendid.equals(getSendId()))
					{
//						System.out.println(System.currentTimeMillis()+" sending: "+getComponentIdentifier());
						send(createAwarenessInfo());
						
						// Additionally send knowns to knowns
						if(getAgent().isRegistry())
						{
							DiscoveryEntry[] kns = getAgent().getKnowns().getEntries();
							for(int i=0; i<kns.length; i++)
							{
								send(kns[i].getInfo());
							}
						}
						
						if(getAgent().getDelay()>0)
							getAgent().doWaitFor(getAgent().getDelay(), this);
					}
					return null;
				}
			});
		}
	}
	
	/**
	 *  Method to send messages.
	 */
	public void send(AwarenessInfo info)
	{
		try
		{
			byte[] data = DiscoveryState.encodeObject(info, getAgent().getMicroAgent().getModel().getClassLoader());
	
//			System.out.println("packet size: "+data.length);

			// Send always to registry.
			if(getAgent().isRegistry())
			{
				sendToKnowns(data);
			}
			else
			{
				sendToRegistry(data);
			}
			
	//		System.out.println("sent: "+address);
	//		System.out.println(getComponentIdentifier()+" sent '"+info+"' ("+data.length+" bytes)");
		}
		catch(Exception e)
		{
			getAgent().getMicroAgent().getLogger().warning("Could not send awareness message: "+e);
//			e.printStackTrace();
		}	
	}
	
	/**
	 *  Get the agent.
	 */
	protected RegistryDiscoveryAgent getAgent()
	{
		return (RegistryDiscoveryAgent)agent;
	}
	
	/**
	 *  Send to registry.
	 */
	protected void sendToRegistry(byte[] data)
	{
//		System.out.println("sent to reg: "+address+" "+port);
		send(data, getAgent().getAddress(), getAgent().getPort());
	}
	
	/**
	 *  Send info to all knowns.
	 *  @param data The data to be send.
	 */
	protected void sendToKnowns(byte[] data)
	{
		DiscoveryEntry[] rems = getAgent().getKnowns().getEntries();
		for(int i=0; i<rems.length; i++)
		{
			InetSocketAddress isa = (InetSocketAddress)rems[i].getEntry();
			send(data, isa.getAddress(), isa.getPort());
		}
//		System.out.println("sent to knwons: "+rems.length);
	}
	
	/**
	 *  Send a packet.
	 */
	public boolean send(byte[] data, InetAddress address, int port)
	{
//		System.out.println("sent packet: "+address+" "+port);
		boolean ret = true;
		try
		{
			DatagramPacket p = new DatagramPacket(data, data.length, new InetSocketAddress(address, port));
			getAgent().getSocket().send(p);
		}
		catch(Exception e)
		{
			ret = false;
		}
		return ret;
	}
}