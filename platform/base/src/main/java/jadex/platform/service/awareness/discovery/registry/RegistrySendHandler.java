package jadex.platform.service.awareness.discovery.registry;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;

import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.service.types.awareness.AwarenessInfo;
import jadex.commons.SUtil;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.transformation.annotations.Classname;
import jadex.platform.service.awareness.discovery.DiscoveryAgent;
import jadex.platform.service.awareness.discovery.DiscoveryEntry;
import jadex.platform.service.awareness.discovery.MasterSlaveSendHandler;

/**
 *  Handle sending.
 */
class RegistrySendHandler extends MasterSlaveSendHandler
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
				.getId().getLocalName());
			this.sendid = sendid;	
			
			getAgent().getMicroAgent().getFeature(IExecutionFeature.class).scheduleStep(new IComponentStep<Void>()
			{
				@Classname("send")
				public IFuture<Void> execute(IInternalAccess ia)
				{
					final Future<Void> ret = new Future<Void>();
					final IComponentStep<Void> step = this;
					
					if(!getAgent().isKilled() && sendid.equals(getSendId()))
					{
//						System.out.println(System.currentTimeMillis()+" sending: "+getComponentIdentifier());
						createAwarenessInfo().addResultListener(agent.getMicroAgent().getFeature(IExecutionFeature.class)
							.createResultListener(new ExceptionDelegationResultListener<AwarenessInfo, Void>(ret)
						{
							public void customResultAvailable(AwarenessInfo info)
							{
								send(info);
								
								// Additionally send all knowns to all other masters and local (not remote) slaves
								if(getAgent().isRegistry())
								{
									DiscoveryEntry[] rems = getAgent().getRemotes().getEntries();
									for(int i=0; i<rems.length; i++)
									{
										send(rems[i].getInfo());
									}
									DiscoveryEntry[] locs = getAgent().getLocals().getEntries();
									for(int i=0; i<locs.length; i++)
									{
										send(locs[i].getInfo());
									}
								}
								
								if(getAgent().getDelay()>0)
									getAgent().doWaitFor(getAgent().getDelay(), step);
							}
						}));
					}
					
					return ret;
				}
			});
		}
	}
	
//	/**
//	 *  Method to send messages.
//	 */
//	public void send(AwarenessInfo info)
//	{
//		try
//		{
//			byte[] data = DiscoveryState.encodeObject(info, getAgent().getMicroAgent().getModel().getClassLoader());
//	
////			System.out.println("packet size: "+data.length);
//
//			// Send always to registry.
//			if(getAgent().isRegistry())
//			{
//				sendToKnowns(data);
//			}
//			else
//			{
//				sendToRegistry(data);
//			}
//			
//	//		System.out.println("sent: "+address);
//	//		System.out.println(getComponentIdentifier()+" sent '"+info+"' ("+data.length+" bytes)");
//		}
//		catch(Exception e)
//		{
//			getAgent().getMicroAgent().getLogger().warning("Could not send awareness message: "+e);
////			e.printStackTrace();
//		}	
//	}
	
	/**
	 *  Method to send messages.
	 */
	public void send(AwarenessInfo info)
	{
		try
		{
			byte[] data = DiscoveryAgent.encodeObject(info, getAgent().getMicroAgent().getClassLoader());
	
//			System.out.println("packet size: "+data.length);

			sendToDiscover(data);
			
			if(getAgent().isRegistry())
			{
				// Distribute to all remote and local platforms.
				sendToRemotes(data);
				sendToLocals(data);
			}
			else if(getAgent().isMaster())
			{
				// As master always send info to registry.
				// Sends its info also to slave to allow local awareness without registry online.
				sendToRegistry(data);
				sendToLocals(data);
			}
			else
			{
				// As slave always send my info to local master.
				sendToMaster(data);
			}
			
//			System.out.println("sent");
//			System.out.println(getComponentIdentifier()+" sent '"+info+"' ("+data.length+" bytes)");
		}
		catch(Exception e)
		{
			agent.getMicroAgent().getLogger().warning("Could not send awareness message: "+e);
//			e.printStackTrace();
		}	
	}
	
	/**
	 *  Send/forward to discover.
	 *  @param data The data to be send.
	 */
	public int sendToDiscover(byte[] data, int maxsend)
	{
		// No discovery.
		return 0;
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
	public void sendToRegistry(byte[] data)
	{
//		System.out.println("sent to reg: "+getAgent().getAddress()+" "+getAgent().getPort());
		send(data, getAgent().getAddress(), getAgent().getPort());
	}
	
	/**
	 *  Send to registry.
	 */
	public void sendToMaster(byte[] data)
	{
//		System.out.println("sent to master: "+SUtil.getInet4Address()+" "+getAgent().getPort());
		send(data, SUtil.getInetAddress(), getAgent().getPort());
	}
	
	/**
	 *  Send awareness info to remote scanner services.
	 *  @param data The data to be send.
	 *  @param maxsend The maximum number of messages to send.
	 */
	public int sendToRemotes(byte[] data, int maxsend)
	{
		if(getAgent().isRegistry())
		{
			return super.sendToRemotes(data, maxsend);
		}
		else
		{
			// For bootstrapping of masters
			// Forward the slave update to registry. 
			// ((MasterSlaveSendHandler)getAgent().getSender()).sendToRemotes(data);
			sendToRegistry(data);
			return 1;
		}
	}
	
//	/**
//	 *  Send info to all knowns.
//	 *  @param data The data to be send.
//	 */
//	protected void sendToKnowns(byte[] data)
//	{
//		DiscoveryEntry[] rems = getAgent().getKnowns().getEntries();
//		for(int i=0; i<rems.length; i++)
//		{
//			InetSocketAddress isa = (InetSocketAddress)rems[i].getEntry();
//			send(data, isa.getAddress(), isa.getPort());
//		}
////		System.out.println("sent to knwons: "+rems.length);
//	}
	
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