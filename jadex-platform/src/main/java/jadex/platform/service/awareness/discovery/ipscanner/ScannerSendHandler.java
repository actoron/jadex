package jadex.platform.service.awareness.discovery.ipscanner;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

import jadex.bridge.service.types.awareness.AwarenessInfo;
import jadex.commons.SUtil;
import jadex.platform.service.awareness.discovery.DiscoveryAgent;
import jadex.platform.service.awareness.discovery.MasterSlaveSendHandler;

/**
 *  Handle sending.
 */
public class ScannerSendHandler extends MasterSlaveSendHandler
{
	/** The current ip to send probes to. */
	protected int currentip;
	
	/** The send count. */
	protected int sendcount;
	
	/**
	 *  Create a new lease time handling object.
	 */
	public ScannerSendHandler(DiscoveryAgent agent)
	{
		super(agent);
	}
	
	/**
	 *  Method to send messages.
	 */
	public void send(AwarenessInfo info)
	{
		try
		{
			byte[] data = DiscoveryAgent.encodeObject(info, getAgent().getDefaultCodecs(), getAgent().getMicroAgent().getClassLoader());
			
			int maxsend = getAgent().getChannel().socket().getSendBufferSize()/data.length;
			int sent = 0;
			
			// Send to all remote other nodes a refresh awareness
			int allowed = maxsend-sent;
			int remotes = 0;
			
			if(getAgent().isMaster())
			{
				if(allowed>0)
				{
					remotes = sendToRemotes(data, allowed);
					sent += remotes;
				}
				
				// Send to all locals a refresh awareness
				sendToLocals(data);
			}
			else
			{
				sendToMaster(data);
			}
			
			// Send to possibly new ones via ip guessing
			if(sendcount%getAgent().getScanFactor()==0)
			{
				allowed = maxsend-sent;
				int discover = 0;
				if(allowed>0)
				{
					discover += sendToDiscover(data, allowed);
					sent+= discover;
				}
			}

//			System.out.println(" sent:"+sent+" remotes: "+remotes);//+" discover: "+discover);
//			System.out.println(getComponentIdentifier()+" sent '"+info+"' ("+data.length+" bytes)");
		}
		catch(Exception e)
		{
			getAgent().getMicroAgent().getLogger().warning("Could not send awareness message: "+e);
//			e.printStackTrace();
		}	
		
		sendcount++;
	}
	
	/**
	 *  Send/forward to discover.
	 *  @param data The data to be send.
	 */
	public int sendToDiscover(byte[] data, int maxsend)
	{
		int ret = 0;
		try
		{
			InetAddress iadr = SUtil.getInetAddress();
			
			if(iadr instanceof Inet4Address)
			{
				short sublen = SUtil.getNetworkPrefixLength(iadr);
				if(sublen==-1) // Guess C class if nothing can be determined.
					sublen = 24;
				byte[] byinet = SUtil.getInetAddress().getAddress();
				int hostbits = 32-sublen;
				int numips = (int)Math.pow(2, hostbits);
				
				int mask = ~(numips-1);
				int iinet = SUtil.bytesToInt(byinet);
				int prefix = iinet & mask;
				
				int ipnum = currentip;
				for(; ret<numips && (maxsend==-1 || ret<maxsend); ret++)
				{
					int iip = prefix | ipnum; 
					byte[] bip = SUtil.intToBytes(iip);
					InetAddress address = InetAddress.getByAddress(bip);
					if(!send(data, address, getAgent().getPort()))
						break;
					
					ipnum = (ipnum+1)%numips;
				}
				currentip = ipnum;
				
	//			System.out.println("sent to discover: "+ret+" "+currentip);
				getAgent().getMicroAgent().getLogger().info("sent to discover: "+ret+" "+currentip);
			}
			else if(iadr instanceof Inet6Address)
			{
				getAgent().getMicroAgent().getLogger().info("Scanning not yet supported for IPV6");
			}
		}
		catch(Exception e)
		{
//			e.printStackTrace();
			getAgent().getMicroAgent().getLogger().warning("Discovery failed: "+e);
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
	 *  Send a packet.
	 */
	public boolean send(byte[] data, InetAddress address, int port)
	{
		boolean ret = true;
		
//		System.out.println("sending to: "+address+" "+port);
		
		try
		{
			ByteBuffer buf = ByteBuffer.allocate(data.length);
//			buf.clear();
			buf.put(data);
			buf.flip();
			int	bytes = getAgent().getChannel().send(buf, new InetSocketAddress(address, port));
			ret = bytes==data.length;
		}
		catch(Exception e)
		{
			// Can happen in case of specific reserved ips, e.g. 0 or 255=broacast.
//			System.out.println("ex: "+address);
		}
		
		return ret;
	}
	
	/**
	 *  Get the agent.
	 */
	protected ScannerDiscoveryAgent getAgent()
	{
		return (ScannerDiscoveryAgent)agent;
	}
}