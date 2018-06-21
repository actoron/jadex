package jadex.platform.service.awareness.discovery;

import java.net.InetAddress;
import java.net.InetSocketAddress;

import jadex.bridge.service.types.awareness.AwarenessInfo;
import jadex.commons.future.IFuture;

/**
 *  Handle sending.
 */
public abstract class MasterSlaveSendHandler extends SendHandler
{
	/**
	 *  Create a new lease time handling object.
	 */
	public MasterSlaveSendHandler(DiscoveryAgent state)
	{
		super(state);
	}
	
	/**
	 *  Create the awareness info.
	 */
	public IFuture<AwarenessInfo> createAwarenessInfo()
	{
		return agent.createAwarenessInfo(AwarenessInfo.STATE_ONLINE, getAgent().createMasterId());
	}
	
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
			
			if(getAgent().isMaster())
			{
				sendToRemotes(data);
				
				// Send to all locals a refresh awareness
				sendToLocals(data);
			}
			else
			{
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
	public int sendToDiscover(byte[] data)
	{
		return sendToDiscover(data, -1);
	}
	
	/**
	 *  Send/forward to discover.
	 *  @param data The data to be send.
	 */
	public abstract int sendToDiscover(byte[] data, int maxsend);
	
	/**
	 *  Send awareness info to remote scanner services.
	 *  @param data The data to be send.
	 *  @param maxsend The maximum number of messages to send.
	 */
	public int sendToRemotes(byte[] data)
	{
		return sendToRemotes(data, -1);
	}
	
	/**
	 *  Send awareness info to remote scanner services.
	 *  @param data The data to be send.
	 *  @param maxsend The maximum number of messages to send.
	 */
	public int sendToRemotes(byte[] data, int maxsend)
	{
		int ret = 0;
		try
		{
			DiscoveryEntry[] rems = getAgent().getRemotes().getEntries();
			for(int i=0; i<rems.length && (maxsend==-1 || ret<maxsend); i++)
			{
				// Only send to remote masters directly.
				// A master will forward a message to its slaves.
				if(rems[i].getInfo().getMasterId()!=null)
				{
					InetSocketAddress sa = (InetSocketAddress)rems[i].getEntry();
					// Use received port, as enables slave to slave communication
					if(!send(data, sa.getAddress(), sa.getPort()))
						break;
					ret++;
				}
			}
			
//			System.out.println("sent to remotes: "+ret+" "+SUtil.arrayToString(remotes));
		}
		catch(Exception e)
		{
			getAgent().getMicroAgent().getLogger().warning("Send to remotes problem:_"+e);
//			e.printStackTrace();
		}
		
		return ret;
	}
	
	/**
	 *  Send to local masters.
	 *  @param data The data to be send.
	 */
	public abstract void sendToMaster(byte[] data);
	
	/**
	 *  Send/forward to locals.
	 *  @param data The data to be send.
	 */
	public void sendToLocals(byte[] data)
	{
		DiscoveryEntry[] locs = getAgent().getLocals().getEntries();
		for(DiscoveryEntry de: locs)
		{
			InetSocketAddress sa = (InetSocketAddress)de.getEntry();
			send(data, sa.getAddress(), sa.getPort());
		}
//		System.out.println("sent to locals: "+locs.length+" "+SUtil.arrayToString(locs));
	}
	
	/**
	 *  Get the agent.
	 */
	protected MasterSlaveDiscoveryAgent getAgent()
	{
		return (MasterSlaveDiscoveryAgent)agent;
	}
	
	/**
	 *  Send a packet.
	 */
	public abstract boolean send(byte[] data, InetAddress address, int port);
}