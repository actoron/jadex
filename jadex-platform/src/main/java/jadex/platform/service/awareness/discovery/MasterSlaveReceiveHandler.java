package jadex.platform.service.awareness.discovery;

import jadex.bridge.service.types.awareness.AwarenessInfo;
import jadex.commons.SUtil;

import java.net.InetAddress;
import java.net.InetSocketAddress;

/**
 *  Master slave receiving handler.
 */
public abstract class MasterSlaveReceiveHandler extends ReceiveHandler
{
	/**
	 *  Create a new receive handler.
	 */
	public MasterSlaveReceiveHandler(MasterSlaveDiscoveryAgent agent)
	{
		super(agent);
	}
	
	/**
	 *  Handle a received packet.
	 */
	public void handleReceivedPacket(InetAddress address, int port, byte[] data, AwarenessInfo info)
	{
//		InetAddress address = packet.getAddress();
//		int port = packet.getPort();
		InetSocketAddress sa = new InetSocketAddress(address, port);
				
//		System.out.println("received: "+info+" "+address+" "+port);
			
		if(info!=null && info.getSender()!=null)
		{
			if(!info.getSender().equals(getAgent().getRoot()))
			{
				announceAwareness(info);
			}
			else
			{
				received_self	= true;
				return;
			}
//			System.out.println(System.currentTimeMillis()+" "+getComponentIdentifier()+" received: "+info.getSender());
		}	

		// Received awareness info
		// When master -> 
		//   if slave info -> save in locals and sent to remote masters and local slaves
		//   if remote info -> save in remotes and send to local slaves
		// When slave ->
		//   save as remote info (also other slaves, for lease time management)
		
		if(getAgent().isMaster())
		{
			if(address.equals(SUtil.getInetAddress()))
			{
				// If awareness message comes from local slave.
				getAgent().getLocals().addOrUpdateEntry(new DiscoveryEntry(info, getAgent().getClockTime(), sa));
				
				// Forward the slave update to remote masters.
				((MasterSlaveSendHandler)getAgent().getSender()).sendToRemotes(data);
			}
			else
			{
				// If awareness message comes from remode node.
				getAgent().getRemotes().addOrUpdateEntry(new DiscoveryEntry(info, getAgent().getClockTime(), sa));
			
				// Forward remote update to local slaves.
				((MasterSlaveSendHandler)getAgent().getSender()).sendToLocals(data);
			}
			
//			System.out.println("to locals, from: "+address+" "+port+" "+info.getSender());
			((MasterSlaveSendHandler)getAgent().getSender()).sendToLocals(data);
		}
		else
		{
			getAgent().getRemotes().addOrUpdateEntry(new DiscoveryEntry(info, getAgent().getClockTime(), sa));
		}
		
//		System.out.println("received awa info: "+getComponentIdentifier().getLocalName()+" "+info.getSender());
	}
	
	/**
	 *  Get the agent.
	 */
	protected MasterSlaveDiscoveryAgent getAgent()
	{
		return (MasterSlaveDiscoveryAgent)agent;
	}
}
