package jadex.platform.service.awareness.discovery.ipbroadcast;

import java.net.DatagramPacket;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;

import jadex.bridge.service.types.awareness.AwarenessInfo;
import jadex.commons.SUtil;
import jadex.commons.future.IFuture;
import jadex.platform.service.awareness.discovery.DiscoveryAgent;
import jadex.platform.service.awareness.discovery.MasterSlaveSendHandler;

/**
 *  Handle sending.
 */
public class BroadcastSendHandler extends MasterSlaveSendHandler
{
	/**
	 *  Create a new lease time handling object.
	 */
	public BroadcastSendHandler(BroadcastDiscoveryAgent state)
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

			// Broadcast info to lan.
			// Does not need to send to known components
			// as broadcast reaches all.
			sendToDiscover(data);
			
			if(getAgent().isMaster())
			{
//				sendToRemotes(data);
				
				// Send to all locals a refresh awareness
				sendToLocals(data);
			}
			else
			{
				// In some networks broadcast might be restricted. Send extra message to master if only unicast works.
				sendToMaster(data);
			}
			
//			System.out.println("sent");
//			System.out.println(agent.getMicroAgent().getComponentIdentifier()+" sent '"+info+"' ("+data.length+" bytes)");
		}
		catch(Exception e)
		{
			agent.getMicroAgent().getLogger().info("Could not send awareness message: "+e);
//			e.printStackTrace();
		}	
	}
	
	/**
	 *  Send/forward to discover.
	 *  @param data The data to be send.
	 */
	public int sendToDiscover(byte[] data, int maxsend)
	{
		int ret = 0;
		
		InetAddress address = SUtil.getInetAddress();
		
		if(address instanceof Inet4Address)
		{
			// Global broadcast address 255.255.255.255 does not work in windows xp/7 :-(
			// http://serverfault.com/questions/72112/how-to-alter-the-global-broadcast-address-255-255-255-255-behavior-on-windows
			// Directed broadcast address = !netmask | IP
//			InetAddress address = InetAddress.getByAddress(new byte[]{(byte)255, (byte)255, (byte)255, (byte)255,});
			
			short prefixlen = SUtil.getNetworkPrefixLength(address);
			if(prefixlen==-1) // Guess C class if nothing can be determined.
				prefixlen = 24;
			
			if(maxsend==-1 || ret<maxsend)
			{
				try
				{
					getAgent().getSocket().send(new DatagramPacket(data, data.length, createBroadcastAddress(address, (short)24), getAgent().getPort()));
					ret++;
				}
				catch(Exception e)
				{
					// Nop if can't send
				}
			}
			if(maxsend==-1 || ret<maxsend)
			{
				try
				{
					getAgent().getSocket().send(new DatagramPacket(data, data.length, createBroadcastAddress(address, (short)16), getAgent().getPort()));
					ret++;
				}
				catch(Exception e)
				{
					// Nop if can't send
				}
			}
			if(maxsend==-1 || ret<maxsend)
			{
				try
				{
					getAgent().getSocket().send(new DatagramPacket(data, data.length, createBroadcastAddress(address, (short)8), getAgent().getPort()));
					ret++;
				}
				catch(Exception e)
				{
					// Nop if can't send
				}
			}
			if((maxsend==-1 || ret<maxsend) && prefixlen!=-1 && prefixlen!=24 && prefixlen!=16 && prefixlen!=8)
			{
				try
				{
					getAgent().getSocket().send(new DatagramPacket(data, data.length, createBroadcastAddress(address, prefixlen), getAgent().getPort()));
					ret++;
				}
				catch(Exception e)
				{
					// Nop if can't send
				}
			}
	//		catch(Exception e)
	//		{
	//			if(!getAgent().isKilled())
	//				getAgent().getMicroAgent().getLogger().warning("Discover error: "+e);
	////			e.printStackTrace();
	//		}
		}
		else if(address instanceof Inet6Address)
		{
			getAgent().getMicroAgent().getLogger().warning("No IPV4 address available, cannot use broadcast. Should use multicast discovery instead.");
		}
		
		return ret;
	}
	
	/**
	 *  Send to local masters.
	 *  @param data The data to be send.
	 */
	public void sendToMaster(byte[] data)
	{
		send(data, SUtil.getInetAddress(), getAgent().getPort());
	}
	
	/**
	 *  Create broadcast address according to prefix length.
	 */
	protected InetAddress createBroadcastAddress(InetAddress address, short prefixlen)
	{
		try
		{
//			InetAddress iadr = SUtil.getInet4Address();
			byte[] byinet = address.getAddress();
			int hostbits = 32-prefixlen;
			int mask = (int)Math.pow(2, hostbits)-1;
			int iinet = SUtil.bytesToInt(byinet);
			int badr = iinet | mask;
			return InetAddress.getByAddress(SUtil.intToBytes(badr));
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
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
	
	/**
	 *  Get the agent.
	 */
	protected BroadcastDiscoveryAgent getAgent()
	{
		return (BroadcastDiscoveryAgent)agent;
	}
}